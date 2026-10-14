package com.bwxor.piejfx.mapper;


import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class FileToGraphicMapper {
    private static final List<String> IMAGE_EXTENSIONS = List.of(
            ".png", ".jpg", ".jpeg", ".gif", ".bmp",
            ".webp", ".tif", ".tiff", ".ico", ".svg",
            ".heic", ".heif", ".avif", ".psd", ".raw");

    private static final String ICON_FOLDER = new String(Character.toChars(0xF749));   // folder (filled)
    private static final String ICON_FILE   = new String(Character.toChars(0xEAA4)); // file
    private static final String ICON_IMAGE  = new String(Character.toChars(0xFA4A));   // photo (filled)
    private static final String ICON_XML    = new String(Character.toChars(0x10170));  // file-code
    private static final String ICON_MD     = new String(Character.toChars(0x10114));  // file-text
    private static final String ICON_CMD    = new String(Character.toChars(0x10196));  // file-settings
    private static final String ICON_JAVA = new String(Character.toChars(0x10009));   // mug (filled)
    private static final String ICON_CSS    = new String(Character.toChars(0xFB08)); // file-type-css
    private static final String ICON_HTML   = new String(Character.toChars(0xFB0C)); // file-type-html
    private static final String ICON_JS     = new String(Character.toChars(0xFB0E)); // file-type-js
    private static final String ICON_TS     = new String(Character.toChars(0xFB17)); // file-type-ts
    private static final String ICON_JSON   = new String(Character.toChars(0xF7B2)); // json
    private static final String ICON_PDF    = new String(Character.toChars(0xFB10)); // file-type-pdf
    private static final String ICON_ZIP    = new String(Character.toChars(0xFB1D)); // file-type-zip  (.zip, .jar)
    private static final String ICON_CSV    = new String(Character.toChars(0xFB09)); // file-type-csv
    private static final String ICON_TXT    = new String(Character.toChars(0xFB19)); // file-type-txt  (.txt, .log)
    private static final String ICON_SQL    = new String(Character.toChars(0xFB15)); // file-type-sql
    private static final String ICON_PY     = new String(Character.toChars(0xED01)); // brand-python
    private static final String ICON_GIT    = new String(Character.toChars(0xEF6F)); // brand-git  (.gitignore, .gitattributes)
    private static final String ICON_KT     = new String(Character.toChars(0xED6D)); // brand-kotlin
    private static final String ICON_CPP    = new String(Character.toChars(0xF5FE)); // brand-cpp
    private static final String ICON_CS     = new String(Character.toChars(0xF003)); // brand-c-sharp
    private static final String ICON_SVG    = new String(Character.toChars(0xFB16)); // file-type-svg
    private static final String ICON_DOCKER = new String(Character.toChars(0xEDCA)); // brand-docker (Dockerfile)
    private static final String ICON_CONFIG = new String(Character.toChars(0x10196)); // file-settings (.properties, .yml, .yaml, .ini)
    private static final String ICON_KEY    = new String(Character.toChars(0xFE28));  // key (.key, .pem)
    private static final String ICON_AUDIO  = new String(Character.toChars(0x10199)); // file-music (.mp3, .wav)
    private static final String ICON_EXE    = new String(Character.toChars(0xECD8));  // brand-windows (.exe, .msi, .com)
    private static final String ICON_LIB    = new String(Character.toChars(0xFD4C));  // library (.dll, .so, .dylib, .lib, .a)
    private static final String ICON_BIN    = new String(Character.toChars(0xEE08));  // binary (.bin, .o, .obj, .elf, .out, .pdb)
    private static final String ICON_PACKAGE = new String(Character.toChars(0xEAFF)); // package (.apk, .deb, .rpm, .appx, .msix)
    private static final String ICON_DISC   = new String(Character.toChars(0xEA90));  // disc (.iso, .img, .dmg)
    private static final String ICON_DB     = new String(Character.toChars(0xF037));  // file-database (.db, .sqlite, .mdb)
    private static final String ICON_FONT   = new String(Character.toChars(0xF041));  // file-typography (.ttf, .otf, .woff)
    private static final String ICON_VIDEO  = new String(Character.toChars(0xEAFA));  // movie (.mp4, .mkv, .avi)
    private static final String ICON_DOC    = new String(Character.toChars(0xFB0A));  // file-type-doc (.doc, .docx, .odt, .rtf)
    private static final String ICON_XLS    = new String(Character.toChars(0xFB1B));  // file-type-xls (.xls, .xlsx, .ods)
    private static final String ICON_PPT    = new String(Character.toChars(0xFB13));  // file-type-ppt (.ppt, .pptx, .odp)

    public static Node mapFileToGraphic(File file) {
        if (file.isDirectory()) {
            return createIcon(ICON_FOLDER, "icon-folder");
        }

        String name = file.getName().toLowerCase(Locale.ROOT);

        // Whole-name matches first: these files have no real extension
        if (name.equals("dockerfile") || name.startsWith("dockerfile.") || name.equals(".dockerignore")) {
            return createIcon(ICON_DOCKER, "icon-docker");
        }
        else if (name.startsWith(".git")) {   // .gitignore, .gitattributes, .gitmodules
            return createIcon(ICON_GIT, "icon-git");
        }
        else if (isImage(name)) {
            return createIcon(ICON_IMAGE, "icon-image");
        }
        else if (hasExtension(name, ".svg")) {
            return createIcon(ICON_SVG, "icon-svg");
        }
        else if (hasExtension(name, ".xml", ".fxml", ".pom")) {
            return createIcon(ICON_XML, "icon-xml");
        }
        else if (hasExtension(name, ".md", ".markdown")) {
            return createIcon(ICON_MD, "icon-md");
        }
        else if (hasExtension(name, ".cmd", ".bat", ".sh", ".ps1")) {
            return createIcon(ICON_CMD, "icon-cmd");
        }
        else if (hasExtension(name, ".java", ".class")) {
            return createIcon(ICON_JAVA, "icon-java");
        }
        else if (hasExtension(name, ".css", ".scss", ".less")) {
            return createIcon(ICON_CSS, "icon-css");
        }
        else if (hasExtension(name, ".html", ".htm")) {
            return createIcon(ICON_HTML, "icon-html");
        }
        else if (hasExtension(name, ".js", ".mjs", ".cjs", ".jsx")) {
            return createIcon(ICON_JS, "icon-js");
        }
        else if (hasExtension(name, ".ts", ".tsx")) {
            return createIcon(ICON_TS, "icon-ts");
        }
        else if (hasExtension(name, ".json")) {
            return createIcon(ICON_JSON, "icon-json");
        }
        else if (hasExtension(name, ".pdf")) {
            return createIcon(ICON_PDF, "icon-pdf");
        }
        else if (hasExtension(name, ".zip", ".jar", ".war", ".7z", ".rar", ".tar", ".gz")) {
            return createIcon(ICON_ZIP, "icon-zip");
        }
        else if (hasExtension(name, ".csv", ".tsv")) {
            return createIcon(ICON_CSV, "icon-csv");
        }
        else if (hasExtension(name, ".txt", ".log", ".lst")) {
            return createIcon(ICON_TXT, "icon-txt");
        }
        else if (hasExtension(name, ".sql")) {
            return createIcon(ICON_SQL, "icon-sql");
        }
        else if (hasExtension(name, ".py")) {
            return createIcon(ICON_PY, "icon-py");
        }
        else if (hasExtension(name, ".kt", ".kts")) {
            return createIcon(ICON_KT, "icon-kt");
        }
        else if (hasExtension(name, ".cpp", ".cc", ".cxx", ".c", ".h", ".hpp")) {
            return createIcon(ICON_CPP, "icon-cpp");
        }
        else if (hasExtension(name, ".cs")) {
            return createIcon(ICON_CS, "icon-cs");
        }
        else if (hasExtension(name, ".properties", ".yml", ".yaml", ".ini", ".toml", ".conf", ".cfg", ".env")) {
            return createIcon(ICON_CONFIG, "icon-config");
        }
        else if (hasExtension(name, ".key", ".pem", ".crt", ".cer", ".p12", ".jks")) {
            return createIcon(ICON_KEY, "icon-key");
        }
        else if (hasExtension(name, ".mp3", ".wav", ".ogg", ".flac", ".m4a")) {
            return createIcon(ICON_AUDIO, "icon-audio");
        }
        else if (hasExtension(name, ".exe", ".msi", ".com")) {
            return createIcon(ICON_EXE, "icon-exe");
        }
        else if (hasExtension(name, ".dll", ".so", ".dylib", ".lib", ".a", ".ocx")) {
            return createIcon(ICON_LIB, "icon-lib");
        }
        else if (hasExtension(name, ".bin", ".o", ".obj", ".elf", ".out", ".pdb")) {
            return createIcon(ICON_BIN, "icon-bin");
        }
        else if (hasExtension(name, ".apk", ".deb", ".rpm", ".appx", ".msix")) {
            return createIcon(ICON_PACKAGE, "icon-package");
        }
        else if (hasExtension(name, ".iso", ".img", ".dmg")) {
            return createIcon(ICON_DISC, "icon-disc");
        }
        else if (hasExtension(name, ".db", ".sqlite", ".sqlite3", ".mdb", ".accdb")) {
            return createIcon(ICON_DB, "icon-db");
        }
        else if (hasExtension(name, ".ttf", ".otf", ".woff", ".woff2", ".eot")) {
            return createIcon(ICON_FONT, "icon-font");
        }
        else if (hasExtension(name, ".mp4", ".mkv", ".avi", ".mov", ".wmv", ".flv", ".webm", ".m4v", ".mpeg", ".mpg")) {
            return createIcon(ICON_VIDEO, "icon-video");
        }
        else if (hasExtension(name, ".doc", ".docx", ".odt", ".rtf")) {
            return createIcon(ICON_DOC, "icon-doc");
        }
        else if (hasExtension(name, ".xls", ".xlsx", ".ods")) {
            return createIcon(ICON_XLS, "icon-xls");
        }
        else if (hasExtension(name, ".ppt", ".pptx", ".odp")) {
            return createIcon(ICON_PPT, "icon-ppt");
        }
        else {
            return createIcon(ICON_FILE, "icon-file");
        }
    }

    private static Label createIcon(String glyph, String styleClass) {
        Label l = new Label(glyph);
        l.getStyleClass().addAll("icon", styleClass);
        return l;
    }

    private static boolean isImage(String name) {
        return IMAGE_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private static boolean hasExtension(String name, String... extensions) {
        for (String extension : extensions) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
