# Contributing

Thank you for your interest in contributing to Spring JavaFX Boot!

## Development Setup

### Prerequisites

- **JDK 17+** on your PATH
- Maven 3.x (wrapper included — use `./mvnw` or `mvnw.cmd`)

### Building

```bash
./mvnw clean package
```

### Running the Demo

**From IDE** — run `io.github.vakho10.springjavafxboot.Launcher` in the `spring-javafx-boot-demo` module.

**From command line:**

```bash
java -jar spring-javafx-boot-demo/target/spring-javafx-boot-demo-1.1.0-SNAPSHOT.jar
```

### Bundling a Native App Image

Build a self-contained executable with a bundled JRE (no Java installation required for end users):

```bash
./mvnw clean package -Pbundle -pl spring-javafx-boot-demo -am
```

The output is in `spring-javafx-boot-demo/target/dist/spring-javafx-boot-demo/` — run the `.exe` directly.

## Documentation

The documentation site is built with [MkDocs Material](https://squidfunk.github.io/mkdocs-material/) and deployed automatically to GitHub Pages.

### Local preview

```bash
pip install -r docs/requirements.txt
mkdocs serve
```

### Deployment

Documentation is auto-deployed on push to `main` when files in `docs/` or `mkdocs.yml` change (via `.github/workflows/docs.yml`).

## Publishing to Maven Central

The project is set up for automated publishing via GitHub Actions. When you push a version tag, the `release.yml` workflow builds, signs, and publishes the **starter** and **archetype** to Maven Central (the demo is excluded).

### One-time setup

1. **Verify your namespace** on the [Sonatype Central Portal](https://central.sonatype.com/):
   - Log in → Namespaces → Add `io.github.vakho10`
   - Sonatype will ask you to create a temporary repo (e.g. a specific repo name) to prove ownership — follow their instructions

2. **Generate a Central Portal token** (Central Portal → Account → Generate User Token) — this gives you a username/password pair

3. **Generate a GPG key** for artifact signing:
   ```bash
   gpg --full-generate-key          # RSA 4096, no expiry is fine
   gpg --list-secret-keys           # note the key ID
   gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
   gpg --armor --export-secret-keys <KEY_ID>   # copy this output
   ```

4. **Add GitHub repository secrets** (Settings → Secrets → Actions):

   | Secret | Value |
   |--------|-------|
   | `MAVEN_CENTRAL_USERNAME` | Token username from step 2 |
   | `MAVEN_CENTRAL_PASSWORD` | Token password from step 2 |
   | `GPG_PRIVATE_KEY` | Full armored private key from step 3 |
   | `GPG_PASSPHRASE` | Passphrase you set in step 3 |

### Releasing a version

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will:

1. Set all module versions to `1.0.0` (strips the `v` prefix)
2. Build with the `release` profile (attaches sources JAR, javadoc JAR, GPG signatures)
3. Publish to Maven Central via the Sonatype Central Publishing plugin
4. Create a GitHub Release with auto-generated release notes

After publishing (~10–30 min for Central to sync), users can add:

```xml
<dependency>
    <groupId>io.github.vakho10</groupId>
    <artifactId>spring-javafx-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
