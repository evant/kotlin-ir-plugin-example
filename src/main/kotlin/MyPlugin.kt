package me.tatarka.kotlinir

import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.phaser.CompilerPhase
import org.jetbrains.kotlin.backend.common.phaser.PhaseConfig
import org.jetbrains.kotlin.backend.common.phaser.PhaserState
import org.jetbrains.kotlin.backend.common.phaser.then
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.extensions.IrLoweringExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

class MyPluginRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        registerProjectExtensions(project as Project, configuration)
    }

    companion object {
        fun registerProjectExtensions(project: Project, configuration: CompilerConfiguration) {
            IrLoweringExtension.registerExtension(project, object : IrLoweringExtension {
                override fun interceptLoweringPhases(phases: CompilerPhase<JvmBackendContext, IrFile, IrFile>): CompilerPhase<JvmBackendContext, IrFile, IrFile> {
                    return super.interceptLoweringPhases(phases)
                        .then(MyPhase())
                }
            })

        }
    }
}

class MyPhase : CompilerPhase<JvmBackendContext, IrFile, IrFile> {
    override fun invoke(
        phaseConfig: PhaseConfig,
        phaserState: PhaserState<IrFile>,
        context: JvmBackendContext,
        input: IrFile
    ): IrFile {
        return input.transform(MyTransform(context), Unit)
    }
}

class MyTransform(val context: JvmBackendContext) : IrElementTransformer<Unit> {
    override fun visitFunction(declaration: IrFunction, data: Unit): IrStatement {
        if (declaration.name.asString() == "magic") {
            return declaration.transform(MagicTransform(context, declaration), Unit)
        } else {
            return super.visitFunction(declaration, data)
        }
    }
}

class MagicTransform(val context: JvmBackendContext, val function: IrFunction) : IrElementTransformer<Unit> {

    override fun visitBody(body: IrBody, data: Unit): IrBody {
        return context.createIrBuilder(
            function.symbol,
            startOffset = function.startOffset,
            endOffset = function.endOffset
        ).irBlockBody {
            +irReturn(irString("magic"))
        }
    }
}