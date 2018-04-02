package org.kotlin.annotationProcessor

import org.yanex.takenoko.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.*

@Target(AnnotationTarget.CLASS)
annotation class TestAnnotation

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.kotlin.annotationProcessor.TestAnnotation")
@SupportedOptions(TestAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class TestAnnotationProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    fun log(line:String) {
        File("./kotlin.log").appendText(line + "\n")
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        log("annotations: $annotations")
        log("roundEnv: $roundEnv")

        val annotatedElements = roundEnv.getElementsAnnotatedWith(TestAnnotation::class.java)
        log("annotatedElements: $annotatedElements")

        if (annotatedElements.isEmpty()) return false

        log("processingEnv: $processingEnv")
        log("processingEnv.options: " + processingEnv.options)

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        log("kaptKotlinGeneratedDir: $kaptKotlinGeneratedDir")

        val generatedKtFile = kotlinFile("test.generated") {
            for (element in annotatedElements) {
                val typeElement = element.toTypeElementOrNull() ?: continue

                property("simpleClassName") {
                    receiverType(typeElement.qualifiedName.toString())
                    getterExpression("this::class.java.simpleName")
                }
            }
        }

        log("generatedKtFile: $generatedKtFile")

        File(kaptKotlinGeneratedDir, "testGenerated.kt").apply {
            parentFile.mkdirs()
            writeText(generatedKtFile.accept(PrettyPrinter(PrettyPrinterConfiguration())))
        }

        return true
    }

    fun Element.toTypeElementOrNull(): TypeElement? {
        if (this !is TypeElement) {
            processingEnv.messager.printMessage(ERROR, "Invalid element type, class expected", this)
            return null
        }

        return this
    }
}