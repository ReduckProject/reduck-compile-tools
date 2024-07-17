### 编译忽略注解处理器

* 引入依赖
```pom
        <dependency>
            <groupId>net.reduck</groupId>
            <artifactId>reduck-compile-tools</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```
实现依赖`sun.tools`，确保配置`JVM_HOME`或者手动引入`${JAVA_HOME}/lib/tools.jar`
```pom
        <dependency>
            <groupId>sun.tools</groupId>
            <artifactId>tools</artifactId>
            <version>1.8</version>
            <scope>system</scope>
            <systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
        </dependency>
```

* 在要忽略的类或者方法上增加注解 - @IgnoreCompile
  - 方法上编译完成后方法会删除
  - 类上编译后会生成一个空方法的类

* 禁用注解处理器

```pom
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <compilerArgs>
                    <!-- 禁用注解处理器 -->
                        <arg>-Acompile.ignore.disabled=true</arg>
                    </compilerArgs>

                    <excludes>
                    <!-- 编译时排除某个类 -->
                        <exclude>com/example/package/to/exclude/AnotherExcludedClass.java</exclude>
                    </excludes>
                </configuration>
            </plugin>

            
            <!-- build-helper-maven-plugin 用于排除指定包名 -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <id>exclude-package</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>remove-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                            <!-- 排除的包名 -->
                                <source>src/main/java/com/example/package/to/exclude</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```

* PS
* 