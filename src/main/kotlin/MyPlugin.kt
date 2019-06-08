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
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.addArguments
import org.jetbrains.kotlin.ir.util.getArguments
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.psi.psiUtil.elementsInRange
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

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
        return input.transform(AssertkAssertTransform(context, input), Unit)
    }
}

class AssertkAssertTransform(val context: JvmBackendContext, val file: IrFile) : IrElementTransformer<Unit> {
    override fun visitCall(expression: IrCall, data: Unit): IrElement {
        if (expression.descriptor.fqNameSafe.asString() == "assertk.assertThat") {
            val name = expression.descriptor.valueParameters.find { it.name.asString() == "name" }!!
            val nameArg = expression.getArguments().find { (d, e) -> d.name.asString() == "name" }
            // skip if name arg is defined
            if (nameArg != null) {
                return super.visitCall(expression, data)
            }
            // Obtain source for expression passed in
            val file = context.psiSourceManager.getKtFile(file)!!
            val sourceExpr = file.elementsInRange(TextRange(expression.startOffset, expression.endOffset))[0]
            var arg = sourceExpr.children[1].children[0]
            // if we have actual = expr, get the second part
            if (arg.children.size > 1) {
                arg = arg.children.last()
            }
            expression.addArguments(
                listOf(
                    name to context.createIrBuilder(expression.symbol).irString(arg.text)
                )
            )
            return super.visitCall(expression, data)
        } else {
            return super.visitCall(expression, data)
        }
    }
}
