package com.example.adorilson.testjavasymbolresolv;

import com.github.javaparser.ParseException;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.SourceFileInfoExtractor;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.AarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    public TypeSolver typeSolver() throws IOException {
        return new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(new File("/Users/federico/repos/TestJavaSymbolSolver/app/src/main/java")),
                new JarTypeSolver("/Users/federico/repos/TestJavaSymbolSolver/libs/android.jar"),
                new AarTypeSolver(new File("/Users/federico/repos/TestJavaSymbolSolver/libs/support-compat-24.2.0.aar")));
    }

    @Test
    public void checkNotificationCompat() throws IOException, ParseException {
        TypeSolver typeSolver = typeSolver();
        ResolvedReferenceTypeDeclaration notificationCompat = typeSolver.solveType("android.support.v4.app.NotificationCompat");
    }

    @Test
    public void checkNotificationCompatBuilder() throws IOException, ParseException {
        TypeSolver typeSolver = typeSolver();
        ResolvedReferenceTypeDeclaration builder = typeSolver.solveType("android.support.v4.app.NotificationCompat.Builder");
    }

    @Test
    public void runAll() throws IOException, ParseException {
        File src = new File("/Users/federico/repos/TestJavaSymbolSolver/app/src/main/java");
        TypeSolver typeSolver = typeSolver();
        SourceFileInfoExtractor sourceFileInfoExtractor = new SourceFileInfoExtractor();
        sourceFileInfoExtractor.setTypeSolver(typeSolver);
        sourceFileInfoExtractor.solve(src);
        System.out.println("OK " + sourceFileInfoExtractor.getOk());
        System.out.println("KO " + sourceFileInfoExtractor.getKo());
        System.out.println("UNSUPPORTED " + sourceFileInfoExtractor.getUnsupported());
    }
}