
Test what happens if the application is already using Java version 21 and 25, the instruction file does not downgrade.

## The Java version in Gradle project can be set in dozens of ways. 

Test some of the below use cases.

Check Java version is upgaded in gradle.properties, build.gradle in the following properties.


// Hard-coded values
// Double quotes also work.
sourceCompatibility = '21'
targetCompatibility = '21'

// Or using JavaVersion enum
sourceCompatibility = JavaVersion.VERSION_21
targetCompatibility = JavaVersion.VERSION_21

// As integers (legacy style)
sourceCompatibility = 21
targetCompatibility = 21

// Using variables
def javaVersion = '21'
sourceCompatibility = javaVersion
targetCompatibility = javaVersion

java {
    // Hard-coded
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    
    // Using variable
    def javaVersion = 21
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
    
    // With vendor specification
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.AMAZON
    }
}


// Can mix both (though toolchain is preferred)
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}



// Reading from project property
sourceCompatibility = project.findProperty('javaVersion') ?: '21'
targetCompatibility = project.findProperty('javaVersion') ?: '21'

// Or with toolchain
java {
    toolchain {
        def version = project.findProperty('javaVersion') ?: '21'
        languageVersion = JavaLanguageVersion.of(version as Integer)
    }
}


build.gradle.kts
// Hard-coded values
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

// Using variables
val javaVersion = "21"
java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
}

// Direct enum usage
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

java {
    // Hard-coded
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    
    // Using variable
    val javaVersion = 21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
    
    // With vendor
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}


### Simple property
javaVersion=21

### Or with more descriptive names
java.version=21
sourceCompatibilityVersion=21
targetCompatibilityVersion=21

### Can also specify toolchain vendor
javaToolchainVersion=21
javaToolchainVendor=AMAZON

### Simple property
javaVersion=21

### Or with more descriptive names
java.version=21
sourceCompatibilityVersion=21
targetCompatibilityVersion=21

### Can also specify toolchain vendor
javaToolchainVersion=21
javaToolchainVendor=AMAZON

ext {
    javaVersion = '21'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion as Integer)
    }
}



## Application has Microservice and Multiple AWS Lambdas in the same Git Repo

Each repo may have different version.


## The submodule build.gradle or build.gradle.kts have plugins and dependencies

The root level build.gradle and build.gradle.kts do not have plugins and dependencies but the sub modules such as "app" or "service" may have it.

Test that the instruction file add these to sub modules as per the instructions.

## Test Switch statement upgrade


## Test instanceof upgrade

