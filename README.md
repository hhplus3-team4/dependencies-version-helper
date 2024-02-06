# Dependencies Version Helper for IntelliJ

## Introduction

Dependencies Version Helper is a plugin for IntelliJ IDEA that assists in managing dependencies in Spring Boot projects. This plugin enables you to easily identify and manage dependencies that need updates by comparing the dependencies defined in your project's `build.gradle` file with the Spring Boot dependency management POM file.

## Key Features

- **Automatic Spring Boot Version Detection**: Automatically detects the Spring Boot version used in the project by analyzing the `build.gradle` file.
- **Dependency Auto-Analysis**: Extracts dependencies from the `build.gradle` file, compares them with the Spring Boot dependency management POM file, and identifies those that need updates.
- **Dependency Update Suggestions**: Presents a list of dependencies that require updates, allowing you to selectively apply these updates.
- **User-Friendly Interface**: Provides a GUI dialog that displays the list of dependencies needing changes.

## Installation

Currently, this plugin can be directly installed through the IntelliJ IDEA plugin repository.

1. Go to `File > Settings > Plugins` in IntelliJ IDEA.
2. Select the `Marketplace` tab and search for "Dependencies Version Helper".
3. Find the plugin and click the `Install` button.
4. Restart IntelliJ to activate the plugin after installation.

## Usage

1. Open a Spring Boot project in IntelliJ IDEA.
2. Select `Tools > Dependencies Version Helper` from the main menu.
3. The plugin will analyze the `build.gradle` file and display a dialog with a list of dependencies that need updates.
4. Use the checkboxes next to each dependency to select items for update, then click `OK` to apply the changes.

## How to Contribute

If you'd like to contribute to this project, you can do so in the following ways:

- **Bug Reporting**: Report bugs via the issue tracker.
- **Feature Suggestions**: If you have ideas for new features, please suggest them through the issue tracker.
- **Code Contributions**: You can directly contribute code via Pull Requests. Please refer to the `CONTRIBUTING.md` document before contributing.

## License

This project is distributed under the MIT License.
