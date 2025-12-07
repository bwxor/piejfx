package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SaveFileUtility {
    /**
     * Saves the content of the CodeArea present at the selected tab. If no file is associated with it, a saveFileAs
     * is triggered.
     *
     * @param tabPane
     * @return true if file has been saved successfully and false otherwise
     */
    public static boolean saveFile(TabPane tabPane) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(tabPane.getSelectionModel().getSelectedIndex());

        if (state.getOpenedFile() == null) {
            return saveFileAs(tabPane);
        } else {
            try (BufferedWriter br = Files.newBufferedWriter(state.getOpenedFile().toPath(), StandardCharsets.UTF_8)) {
                br.write(state.getContent());
            } catch (IOException e) {
                // ToDo: Show an error
                throw new RuntimeException(e);
            }

            state.setSaved(true);
            tabPane.getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
        }

        return true;
    }

    /**
     * Opens a @ref FileChooser and saves the content of the selected tab into the chosen file.
     *
     * @param tabPane
     * @return true if a file has been chosen and false otherwise
     */
    public static boolean saveFileAs(TabPane tabPane) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(tabPane.getSelectionModel().getSelectedIndex());

        var fileChooser = new FileChooser();

        // ToDo: This needs to get externalized :)
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text documents (*.txt)", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML files (*.html, *.htm)", "*.html", "*.htm"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSS files (*.css)", "*.css"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JavaScript files (*.js, *.mjs)", "*.js", "*.mjs"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TypeScript files (*.ts, *.tsx)", "*.ts", "*.tsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSX files (*.jsx)", "*.jsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PHP files (*.php)", "*.php"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java files (*.java)", "*.java"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Kotlin files (*.kt, *.kts)", "*.kt", "*.kts"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scala files (*.scala)", "*.scala"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Groovy files (*.groovy)", "*.groovy"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("C files (*.c, *.h)", "*.c", "*.h"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("C++ files (*.cpp, *.hpp, *.cc, *.cxx, *.hxx)", "*.cpp", "*.hpp", "*.cc", "*.cxx", "*.hxx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("C# files (*.cs)", "*.cs"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Objective-C files (*.m, *.mm)", "*.m", "*.mm"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Visual Basic files (*.vb)", "*.vb"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("F# files (*.fs, *.fsx)", "*.fs", "*.fsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Python files (*.py, *.pyw)", "*.py", "*.pyw"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ruby files (*.rb)", "*.rb"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Go files (*.go)", "*.go"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Rust files (*.rs)", "*.rs"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Swift files (*.swift)", "*.swift"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Perl files (*.pl, *.pm)", "*.pl", "*.pm"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lua files (*.lua)", "*.lua"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Shell scripts (*.sh, *.bash)", "*.sh", "*.bash"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PowerShell scripts (*.ps1, *.psm1)", "*.ps1", "*.psm1"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Batch files (*.bat, *.cmd)", "*.bat", "*.cmd"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Haskell files (*.hs)", "*.hs"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Erlang files (*.erl)", "*.erl"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Elixir files (*.ex, *.exs)", "*.ex", "*.exs"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clojure files (*.clj, *.cljs)", "*.clj", "*.cljs"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OCaml files (*.ml, *.mli)", "*.ml", "*.mli"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Common Lisp files (*.lisp, *.lsp)", "*.lisp", "*.lsp"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scheme files (*.scm, *.ss)", "*.scm", "*.ss"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clojure files (*.clj)", "*.clj"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("YAML files (*.yaml, *.yml)", "*.yaml", "*.yml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TOML files (*.toml)", "*.toml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md, *.markdown)", "*.md", "*.markdown"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("LaTeX files (*.tex)", "*.tex"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly files (*.asm, *.s)", "*.asm", "*.s"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("D files (*.d)", "*.d"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zig files (*.zig)", "*.zig"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dart files (*.dart)", "*.dart"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("R files (*.r, *.R)", "*.r", "*.R"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MATLAB files (*.m)", "*.m"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Julia files (*.jl)", "*.jl"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Nim files (*.nim)", "*.nim"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Crystal files (*.cr)", "*.cr"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("V files (*.v)", "*.v"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("INI files (*.ini)", "*.ini"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Properties files (*.properties)", "*.properties"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Environment files (*.env)", "*.env"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WebAssembly files (*.wasm, *.wat)", "*.wasm", "*.wat"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pascal files (*.pas)", "*.pas"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Delphi files (*.dpr, *.dfm)", "*.dpr", "*.dfm"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fortran files (*.f90, *.f95, *.f03)", "*.f90", "*.f95", "*.f03"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("COBOL files (*.cbl, *.cob)", "*.cbl", "*.cob"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ada files (*.adb, *.ads)", "*.adb", "*.ads"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*"));

        File selectedFile = fileChooser.showSaveDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            state.setOpenedFile(selectedFile);
            return saveFile(tabPane);
        }

        return false;
    }
}
