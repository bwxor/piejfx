# Adding Syntax Highlighting for a Specific Language

This describes how syntax highlighting works in the current editor and how to add support for a new language. It is based on the current code: no code was changed to produce it.

## How highlighting works today

1. **Editor widget.** Each editor tab is a RichTextFX `CodeArea` created in `TabFactory.createEditorTab()`.
2. **Grammar selection.** When a file is opened, `EditorTabPaneService` and `FileService` call `GrammarService.setGrammarToCodeArea(codeArea, file)`. This takes the file extension (the text after the last `.`) and calls `loadGrammar(extension)`.
3. **Grammar lookup.** `loadGrammar` scans the grammars directory (`AppDirConstants.GRAMMARS_DIR`, which is `<config dir>/grammars`). It picks the first JSON file whose `"extensions"` array contains `"." + extension`.
4. **Rule parsing.** Each entry in `"rules"` becomes a `GrammarRule` (a compiled regex with `MULTILINE` plus a `type` string). `"autocomplete"` becomes the word list used by `AutofillChangeListener`.
5. **Highlighting.** On every text change, `GrammarService` waits 100 ms (debounce), then runs `computeHighlighting()` over the whole text. Each regex match becomes a style span whose CSS class is the rule's `type`.
6. **Colours.** The theme CSS (`config/themes/dark.css` and `light.css`) maps those classes, for example `.styled-text-area .keyword { -fx-fill: syn-keyword; }`.

## Option 1: Add a grammar file (no Java changes)

This works for any language whose tokens can be described with regular expressions.

1. Create `<language>.json` in the grammars folder. Use `java.json` or `python.json` as a template:

   ```json
   {
     "extensions": [".xyz"],
     "rules": [
       { "type": "comment", "regex": "//[^\\n]*" },
       { "type": "string",  "regex": "\"([^\"\\\\]|\\\\.)*\"" },
       { "type": "keyword", "regex": "\\b(if|else|return)\\b" },
       { "type": "number",  "regex": "\\b\\d+\\b" }
     ],
     "autocomplete": ["if", "else", "return"]
   }
   ```

2. Make sure the `type` names match CSS classes that exist in the themes (`keyword`, `string`, `comment`, `number`, `annotation`, `function`, `type`, `operator`, `punctuation`, `boolean`, `char`, and so on). Check the `.styled-text-area .<name>` rules in `dark.css` and `light.css`.
3. If you add a new type name, add a matching `.styled-text-area .<name>` rule to **both** theme files. Otherwise the tokens show in the default text colour.
4. Restart the app or reopen the file. The grammar is loaded when a tab is opened, so already-open tabs keep their old grammar.

**Rule priority.** Rules are applied in the order they appear in the file. A match is skipped if it overlaps a match already recorded. Put the most specific rules first. For example, put strings and comments before keywords, so keywords inside strings are not highlighted.

**Before you rely on it,** confirm that the bundled grammars in `src/main/resources/.../config/grammars` are copied into the runtime config directory at startup. I did not check this, and it decides whether your new file is picked up from the resources folder or only from the user's config folder.

## Option 2: Change the code

Use this if you need more than regex rules, for example to match a language's context-sensitive syntax.

- **Extension matching.** Matching is by extension only. Files with no extension (such as `Makefile`) cannot match. To support them, change `hasExtension()` and `setGrammarToCodeArea()` so they match on file name as well.
- **Richer rules.** Add an optional field to the grammar JSON, such as `"group"` for regex capture groups or `"multiline": true` for constructs that span lines, and handle it in `loadGrammarRules()` and `computeHighlighting()`.
- **Explicit mapping.** Add a Java `Map<String, String>` from extension to grammar file, which is clearer than scanning every file on every open.
- **Caching.** `loadGrammar()` re-reads and re-parses the JSON file for each tab. Cache the compiled `Grammar` objects per extension.
- **Libraries.** For a full parser (for example, a TextMate or tree-sitter grammar), RichTextFX's `StyleSpans` API stays the same. You would only replace the `computeHighlighting()` step.

## Notes and limitations

- Highlighting is recomputed for the whole document on each change. This is fine for typical files but may slow down very large files.
- Regexes are compiled with `MULTILINE` only, not `DOTALL`. Use `[\s\S]` instead of `.` when a pattern must cross line breaks.
- An invalid regex in a grammar file will throw when the file is loaded. Test new patterns before you ship them.
- Autocomplete uses the same grammar file. A language with no `"autocomplete"` key gets highlighting but no word completion.
