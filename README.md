# MPC Android SDK Sample App

This is a sample application demonstrating the usage of the MPC Android SDK and ActiveCard SDK.

## Project Structure

```
project/                 # Parent directory
├── demo/                # Demo app
├── sdk/                 # SDK module (from mpc-android-sdk)
├── activecard/          # ActiveCard module (from activecard-android-sdk)
└── repo/                # Repository module (from mpc-android-sdk)
```

## Setup Instructions

Important Notes:
- The `sdk` and `repo` directories should be at the same level as the `activecard` directory
- The `settings.gradle.kts` file is configured to look for modules in these specific locations
- Make sure all directories are at the same level in the parent directory
- Ensure you're on the correct branches:
  - MPC Android SDK: `feature/active-card`
  - ActiveCard SDK: `dev`

To set up the project correctly, follow these steps:

1. Create a parent directory for all components:
```bash
mkdir project
cd project
```

2. Clone the main MPC Android SDK repository and checkout the correct branch:
```bash
git clone https://github.com/Cramiumlabs/mpc-android-sdk.git
cd mpc-android-sdk
git checkout feature/active-card
cd ..
```

3. Clone the ActiveCard SDK repository and checkout the correct branch:
```bash
git clone https://github.com/Cramiumlabs/activecard-android-sdk.git
cd activecard-android-sdk
git checkout dev
cd ..
```

Your final directory structure should look like this:
```
project/                 # Parent directory
├── demo/                # Demo app
├── sdk/                 # SDK module (from mpc-android-sdk)
├── activecard/          # ActiveCard module (from activecard-android-sdk)
└── repo/                # Repository module (from mpc-android-sdk)
```

## Building the Project

1. Open the project in Android Studio by opening the `demo` directory
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