# A simple Ghidra plugin to keep notes while reverse engineering

This Ghidra plugin adds a text editor area that can be used to keep notes while reverse engineering a binary.  The interesting bit is that notes are stored *within* the Ghidra program database and are automatically synchronised upon closing.

This is more or less a proof of concept right now but there are plans to add more functionality like binding notes to functions, variables, or locations, markdown/rich text support, text export, a proper UI and so on.

## How to build

After you checked the project out from Git, run

```bash
./gradlew buildExtension -PGHIDRA_INSTALL_DIR=<Your Ghidra installation path>
```

and if everything went fine, you'll find the compiled plugin in the `dist` directory.

## How to install

Run Ghidra, then from the `File` menu choose `Install Extensions`, click on the plus icon, and select the compiled file built earlier.

## How to use

Open a program in CodeBrowser, from the `File` menu choose `Configure Tool`, go to the `Experimental` entry and click its `Configure` button, then enable the notes plugin from there.

## Licences and copyright

* The text editor is [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea/), licensed under the BSD-3 licence.
* The Ghidra plugin itself is licensed under the Apache 2.0 licence, available in the repository as `LICENCE.txt`.

Where it applies: Copyright 2023 Alessandro Gatti - frob.it
