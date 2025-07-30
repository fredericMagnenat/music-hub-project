# Maven Multi-Module Best Practices

For our multi-module Maven project, the following practices are critical to ensure consistency, maintainability, and efficient dependency management:

1.  **Centralized Dependency Management (`dependencyManagement`)**:
    *   All versions for dependencies and plugins, especially those related to Quarkus (e.g., `quarkus-bom`), **must** be declared in the `<dependencyManagement>` section of the root `apps/backend/pom.xml`.
    *   This ensures that all sub-modules inherit the same, consistent versions, preventing version conflicts and simplifying upgrades.

2.  **Inheritance of Dependencies**:
    *   When a dependency's version is managed in the parent's `dependencyManagement`, sub-modules **must not** specify the `<version>` tag for that dependency. Maven will automatically inherit the version from the parent.
    *   Example: If `quarkus-wiremock` is in parent's `dependencyManagement`, a sub-module should declare it as:
        ```xml
        <dependency>
            <groupId>io.quarkiverse.wiremock</groupId>
            <artifactId>quarkus-wiremock</artifactId>
            <scope>test</scope>
        </dependency>
        ```

3.  **`quarkus-bom` Import**:
    *   The `quarkus-bom` (Bill of Materials) **must** be imported only once in the `<dependencyManagement>` section of the root `apps/backend/pom.xml` with `<scope>import</scope>` and `<type>pom</type>`.
    *   It **must not** be imported in any sub-module's `pom.xml`.

4.  **Centralized Plugin Management and Execution (`pluginManagement` & Parent `<plugins>`)**:
    *   All Maven plugins, especially the `quarkus-maven-plugin`, **must** be declared with their versions and default configurations in the `<pluginManagement>` section of the root `apps/backend/pom.xml`.
    *   For plugins that should apply to all (or most) sub-modules (e.g., `maven-compiler-plugin`, `maven-surefire-plugin`, `maven-failsafe-plugin`, `jandex-maven-plugin`), they **should also be declared in the `<plugins>` section directly under the `<build>` tag of the root `apps/backend/pom.xml`**.
    *   This ensures that all modules use the same version and configuration for these plugins, leading to consistent build behavior across the project, and **they are automatically applied to all child modules**.
    *   **Example Configuration in Parent POM (`apps/backend/pom.xml`)**:
        ```xml
        <pluginManagement>
            <plugins>
                <!-- All plugin versions and default configurations go here -->
                <plugin>
                    <groupId>${quarkus.platform.group-id}</groupId>
                    <artifactId>quarkus-maven-plugin</artifactId>
                    <version>${quarkus.platform.version}</version>
                    <extensions>true</extensions>
                    <configuration>
                        <skip>${quarkus.build.skip}</skip>
                    </configuration>
                    <executions>
                        <execution>
                            <goals>
                                <goal>build</goal>
                                <goal>generate-code</goal>
                                <goal>generate-code-tests</goal>
                                <goal>native-image-agent</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>${compiler-plugin.version}</version>
                    <configuration>
                        <compilerArgs>
                            <arg>-parameters</arg>
                        </compilerArgs>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok-mapstruct-binding</artifactId>
                                <version>${lombok-mapstruct-binding.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>${surefire-plugin.version}</version>
                    <configuration>
                        <systemPropertyVariables>
                            <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
                            <maven.home>${maven.home}</maven.home>
                        </systemPropertyVariables>
                    </configuration>
                </plugin>
                <plugin>
                    <artifactId>maven-failsafe-plugin</artifactId>
                    <version>${surefire-plugin.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>integration-test</goal>
                                <goal>verify</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <systemPropertyVariables>
                            <native.image.path>
                                ${project.build.directory}/${project.build.finalName}-runner</native.image.path>
                            <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
                            <maven.home>${maven.home}</maven.home>
                        </systemPropertyVariables>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>io.smallrye</groupId>
                    <artifactId>jandex-maven-plugin</artifactId>
                    <version>${jandex-plugin.version}</version>
                    <executions>
                        <execution>
                            <id>make-index</id>
                            <goals>
                                <goal>jandex</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-jar-plugin</artifactId>
                    <version>3.4.2</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>test-jar</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>0.8.13</version>
                </plugin>
            </plugins>
        </pluginManagement>

        <!-- Plugins declared here are automatically applied to all child modules -->
        <plugins>
            <plugin>
                <groupId>${quarkus.platform.group-id}</groupId>
                <artifactId>quarkus-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
            </plugin>
            <plugin>
                <artifactId>maven-failsafe-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>io.smallrye</groupId>
                <artifactId>jandex-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <artifactId>maven-jar-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
            </plugin>
        </plugins>
        ```

5.  **Minimal Plugin Declarations in Child Modules**:
    *   Child modules **should generally NOT include a `<build><plugins>` section** unless they require a very specific plugin configuration that deviates from the parent's default.
    *   This significantly reduces verbosity and improves maintainability.
    *   Example of a typical child module `pom.xml` (e.g., `artist-domain/pom.xml`):
        ```xml
        <project>
            <!-- ... parent and artifact details ... -->
            <dependencies>
                <!-- ... module-specific dependencies ... -->
            </dependencies>
            <!-- No <build><plugins> section needed for common plugins -->
        </project>
        ```

6.  **Skipping Quarkus Build for Non-Application Modules (`quarkus.build.skip`)**:
    *   For Maven modules that are not directly deployable Quarkus applications (e.g., domain modules, SPI modules, wiring modules), the `<quarkus.build.skip>true</quarkus.build.skip>` property **should** be added within their `<properties>` section. This prevents Quarkus from attempting to build an application for these modules, optimizing build times.
    *   The root `apps/backend/pom.xml` (or any module that *is* a deployable Quarkus application, like `bootstrap`) **must** ensure that `quarkus.build.skip` is either `false` or not set (defaulting to `false`).
