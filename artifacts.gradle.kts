//create<Jar>

//task buildGrammar_kit_jar (type: Jar, dependsOn: [rootProject.tasks['assemble']]) {
//  archiveBaseName = 'grammar-kit'
//  destinationDirectory = file(artifactsPath)
//  manifest { from "$rootDir/resources/META-INF/MANIFEST.MF" }
//  from (rootProject.sourceSets.main.output)
//  from (file ("$rootDir/src/org/intellij/grammar/parser/GeneratedParserUtilBase.java")) {
//    into '/templates'
//  }
//}
//
//task buildGrammar_kit_zip (type: Zip, dependsOn: [buildGrammar_kit_jar]) {
//  archiveBaseName = 'GrammarKit'
//  destinationDirectory = file(artifactsPath)
//  from (tasks['buildGrammar_kit_jar'].outputs) {
//    into '/GrammarKit/lib'
//  }
//}
//
//task buildExpression_console_sample (type: Jar) {
//  archiveBaseName = 'expression-console-sample'
//  destinationDirectory = file(artifactsPath)
//  manifest { from "$rootDir/tests/org/intellij/grammar/expression/META-INF/MANIFEST.MF" }
//  from (files("$rootDir/out/test/grammar-kit/org/intellij/grammar/expression")) {
//    into '/org/intellij/grammar/expression'
//  }
//}
//
//task artifacts (dependsOn: [buildGrammar_kit_jar, buildGrammar_kit_zip, buildExpression_console_sample]) { }
