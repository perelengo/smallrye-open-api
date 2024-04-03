package io.smallrye.openapi.runtime.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.Type;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;

import io.smallrye.openapi.api.models.parameters.ParameterImpl;

public class JavadocUtils {

    @SuppressWarnings("unchecked")
    public static ClassJavadoc loadJavadoc(ClassLoader loader, ClassInfo resourceClass) {
        ClassJavadoc classDoc = null;
        try {
            Class resourceImplClass = loader.loadClass(resourceClass.name().toString());
            Class[] interfaces = resourceImplClass.getInterfaces();
            for (Class interf : interfaces) {
                Method[] methods = interf.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent((Class<? extends Annotation>) Class.forName("javax.ws.rs.GET"))
                            || method.isAnnotationPresent((Class<? extends Annotation>) Class.forName("javax.ws.rs.POST"))
                            || method.isAnnotationPresent((Class<? extends Annotation>) Class.forName("javax.ws.rs.PUT"))
                            || method.isAnnotationPresent((Class<? extends Annotation>) Class.forName("javax.ws.rs.DELETE"))) {
                        classDoc = RuntimeJavadoc.getJavadoc(interf.getName(), loader);
                        break;
                    }
                }
            }
            if (classDoc == null)
                classDoc = RuntimeJavadoc.getJavadoc(resourceClass.name().toString(), loader);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }
        return classDoc;
    }

    public static boolean matches(MethodJavadoc javaDocMethod, MethodInfo method) {
        if (method.isConstructor()) {
            return javaDocMethod.isConstructor()
                    && paramsMatch(javaDocMethod.getParamTypes().toArray(new String[] {}),
                            method.parameterTypes().toArray(new Type[] {}));
        } else {
            return method.name().equals(javaDocMethod.getName())
                    && paramsMatch(javaDocMethod.getParamTypes().toArray(new String[] {}),
                            method.parameterTypes().toArray(new Type[] {}));
        }
    }

    private static boolean paramsMatch(String[] javadocParams, Type[] params) {
        return Arrays.equals(getCanonicalNames(params), javadocParams);
    }

    private static String[] getCanonicalNames(Type[] params) {
        List<String> methodParamsTypes = new ArrayList<>();
        for (Type aClass : params) {
            methodParamsTypes.add(aClass.name().toString());
        }
        return methodParamsTypes.toArray(new String[] {});
    }

    public static boolean matches(ParamJavadoc p, ParameterImpl parameter) {
        return (p.getName() == null && parameter.getName() == null)
                || (p.getName().equals(parameter.getName()));
    }

    public static boolean matches(ParamJavadoc p, MethodParameterInfo parameter) {
        return (p.getName() == null && parameter.name() == null)
                || (p.getName().equals(parameter.name()));
    }

    public static boolean matches(ParamJavadoc p, Parameter parameter) {
        return (p.getName() == null && parameter.getName() == null)
                || (p.getName().equals(parameter.getName()));
    }

    public static boolean matches(FieldJavadoc m, FieldInfo fieldInfo) {
        return (m.getName() == null && fieldInfo.name() == null)
                || (m.getName().equals(fieldInfo.name()));
    }

}
