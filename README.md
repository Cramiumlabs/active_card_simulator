# MPC Android SDK Sample App

This is a sample application demonstrating the usage of the MPC Android SDK and ActiveCard SDK.

## Project Structure

```
mpc-android-sdk/
├── .git/
├── .DS_Store
├── README.md
├── activecard/
│   ├── .git/
│   ├── .DS_Store
│   ├── build/
│   ├── src/
│   ├── docs/
│   ├── build.gradle.kts
│   ├── consumer-rules.pro
│   ├── proguard-rules.pro
│   └── .gitignore
├── sample/
│   ├── .git/
│   ├── .idea/
│   ├── app/
│   ├── .gradle/
│   ├── gradle/
│   ├── .kotlin/
│   ├── .gitignore
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── local.properties
│   ├── gradlew
│   ├── gradlew.bat
│   └── gradle.properties
├── sdk/
│   ├── build/
│   ├── src/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   ├── consumer-rules.pro
│   └── .gitignore
└── repo/
```

## Setup Instructions

To set up the project correctly, follow these steps:

1. Create a parent directory for all components:
```bash
mkdir project
cd project
```

2. Clone the main MPC Android SDK repository:
```bash
git clone https://github.com/Cramiumlabs/mpc-android-sdk.git .
```

3. Clone the ActiveCard SDK repository into the `activecard` directory:
```bash
git clone https://github.com/Cramiumlabs/activecard-android-sdk.git activecard
```

Your final directory structure should look like this:
```
project/           # Parent directory
├── sample/               # Sample app (from mpc-android-sdk)
├── sdk/                 # SDK module (from mpc-android-sdk)
├── activecard/          # ActiveCard module (from activecard-android-sdk)
└── repo/                # Repository module (from mpc-android-sdk)
```

Important Notes:
- The `sample`, `sdk`, and `repo` directories should be at the same level as the `activecard` directory
- The `settings.gradle.kts` file is configured to look for modules in these specific locations
- Make sure all directories are at the same level in the parent directory

## Building the Project

1. Open the project in Android Studio by opening the `sample` directory
2. Sync the project with Gradle files
3. Build and run the sample app

## Requirements

- Android Studio Arctic Fox or newer
- Android SDK 21 or higher
- Gradle 7.0 or higher

## License

Please refer to the respective repositories for license information:
- [MPC Android SDK](https://github.com/Cramiumlabs/mpc-android-sdk)
- [ActiveCard Android SDK](https://github.com/Cramiumlabs/activecard-android-sdk) 