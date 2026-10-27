
package Main.main.meow.client.module.modules.hud.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * this serves no purpose to users of the mod; rather, the developers who add hud elements are forced to add a color setting for the text and background
 * to ensure that each hud module can have a customizable background and customizable text to maintain consistency.
 */

@SupportedAnnotationTypes("Main.main.meow.client.module.modules.hud.annotation.RequireHudModuleFields") // what annotation is processed
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public final class RequireHudModuleFieldsProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        // elements associated with RequireHudModuleFields have their fields searched that match to the string in each call
        for (Element element : roundEnvironment.getElementsAnnotatedWith(processingEnv.getElementUtils().getTypeElement("Main.main.meow.client.module.modules.hud.annotation.RequireHudModuleFields"))) {
            if (element instanceof TypeElement typeElement) {
                checkField(typeElement, "BACKGROUND_COLOR");
                checkField(typeElement, "TEXT_COLOR");
            }
        }
        return true;
    }

    private void checkField(TypeElement type, String fieldToCheck) {
        // on the event that a field matches the desired field we want (BACKGROUND_COLOR and TEXT_COLOR), we ensure that it contains the exact parameters:
        // private static final
        for (VariableElement field : ElementFilter.fieldsIn(type.getEnclosedElements())) {
            if (!field.getSimpleName().contentEquals(fieldToCheck)) {
                continue;
            }

            Set<Modifier> modifiers = field.getModifiers();
            if (!modifiers.contains(Modifier.PRIVATE) || !modifiers.contains(Modifier.STATIC) || !modifiers.contains(Modifier.FINAL) || !field.asType().toString().equals("Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting")) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, fieldToCheck + " must be private static final ColorSetting", field);
            }
            return;
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Missing required field: " + fieldToCheck, type);
    }
}
