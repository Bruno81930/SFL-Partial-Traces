# Spectrum-Based Fault Diagnosis with Partial Traces – Replication Package

This repository provides the source code, data parsers and evaluation scripts used in our paper. It implements three main approaches to spectrum‑based fault localization when only partial execution traces are available, and explores trace reconstruction techniques to recover missing information. The goal of this replication package is to help reviewers inspect, understand, and reproduce the results reported in the manuscript.

## Approaches

1. **Baseline**  
   Runs a standard SFL algorithm on the partial execution traces without any reconstruction. Serves as the control approach.

2. **Rec‑Min**  
   Minimum reconstruction: adds only those components that are “unavoidable” in the program graph (i.e., appear on all possible execution paths between observed components). Exposed via the `reconstructed` CLI sub‑command; implemented in `reconstruction` package using `GraphTechnique`.

3. **Rec‑Weighted**  
   Probabilistic reconstruction (SFL⁺): assigns a likelihood to each missing component based on random‑walk or weighted graph analysis, then feeds these probabilities into an adapted SFL algorithm. Exposed via the `sfl+` CLI sub‑command; implemented in `SFLPlusAlgorithm`.

4. **Rec‑Max**  
   Adds every component that appears on at least one execution path. This approach, also discussed in the paper, is **implemented** in the same location as Rec‑Min, under the `reconstruction` package using the `GraphTechnique` class.


---

## Repository Structure

```
scripts/               # Python/Shell scripts to prepare the dataset
source/                # Java source code and build files
├─ pom.xml             # Maven build descriptor
├─ scripts/            # Shell wrappers for running experiments
└─ src/
   └─ main/java/org/diagnosis/
      ├─ algorithms/      # Core algorithms (baseline, reconstruction, SFL+)
      │  ├─ entities/     # Data types (Component, HitVector, TestCase)
      │  ├─ filter/       # Filtering strategies
      │  ├─ parser/       # Parsers for traces and logs
      │  ├─ reconstruction/# Reconstruction techniques & graph infra
      │  └─ sfl/          # SFL formulas & computations
      ├─ app/             # CLI interfaces and evaluation models
      │  ├─ cli/          # Picocli commands
      │  ├─ model/        # Evaluation classes
      │  └─ evaluation/   # Metrics and report generation
      └─ test/            # Unit tests
```

The top‑level `scripts/` folder contains data‑generation scripts used in the paper. These can be executed independently (see “Generating the dataset” below).

---

## Data Format and Preparation

Each bug’s data directory should look like:

```
data/<project-name>/<bug-id>/
├─ all_tests.txt       # All executed tests (one per line: “<Class> <method>”)
├─ failed_tests.txt    # Subset of failing tests
├─ ground_truth.txt    # Fully‑qualified fixed method(s), one per line
├─ traces/             # XML trace file per test (JaCoCo‑like format)
└─ repo/               # Local checkout of the buggy project source
```

- **all_tests.txt**: whitespace‑separated “Class method” entries.
- **failed_tests.txt**: subset of `all_tests.txt` corresponding to failed tests.
- **ground_truth.txt**: each line is `package.Class::method`.
- **traces/**: XML files named `<Class>_<method>.xml` or `<package.Class>_<method>.xml`.
- **repo/**: project source used to build the call graph for reconstruction.

---

## Sources List

Some CLIs accept a `--source` argument, an array of root packages to include when building the execution graph and computing rankings. For example:

```
--source org.apache.commons.lang org.apache.commons.text
```

---

## Generating the Dataset

Use the Python and shell scripts in `scripts/` to reproduce the derived data files:

1. **Collect raw artifacts**  
   Place buggy project source and execution logs in a working directory.

2. **Extract partial traces**  
   ```bash
   python scripts/parse_logs.py      --input /path/to/raw/logs      --output /path/to/output/traces
   ```

3. **Generate test lists**  
   ```bash
   python scripts/generate_test_lists.py      --project-source /path/to/source      --traces /path/to/output/traces      --all-tests /path/to/output/all_tests.txt      --failed-tests /path/to/output/failed_tests.txt
   ```

4. **Create ground‑truth annotations**  
   ```bash
   python scripts/derive_ground_truth.py      --repo /path/to/source      --fix-commit <commit-hash>      --output /path/to/output/ground_truth.txt
   ```

After these steps, ensure the directory layout matches the one described above, then point the CLI commands at the root dataset.

---

## Building the Project

Requires **Java 17** and **Maven**. From the `source/` directory:

```bash
cd source
mvn clean package
```

Produces a self‑contained JAR in `target/diagnosis-<version>-jar-with-dependencies.jar`.

### Example `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.diagnosis</groupId>
  <artifactId>diagnosis</artifactId>
  <version>1.0.0</version>
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
  </properties>
  <dependencies>
    <!-- Picocli, SLF4J, JGraphT, etc. -->
  </dependencies>
  <build>
    <plugins>
      <!-- Compiler and Assembly plugins -->
    </plugins>
  </build>
</project>
```

---

## Running the Experiments

Replace `<project>`, `<bug>`, and `<data>` with your values.

### Baseline (partial trace)

```bash
java -jar target/diagnosis-1.0.0-jar-with-dependencies.jar   run partial   -p <project> -b <bug>   -P 0.7 -f RANDOM   -d /path/to/data   -t 5 -r -l
```

- `-P`: fraction of components retained (e.g. `0.7` = 70%).
- `-f`: `RANDOM` or `RANDOM_WITH_FAULTS`.
- `-t`: number of Monte‑Carlo iterations.
- `-r`: write Excel reports.
- `-l`: local console output.

### Rec‑Min (reconstructed)

```bash
java -jar target/diagnosis-1.0.0-jar-with-dependencies.jar   run reconstructed   -p <project> -b <bug>   -P 0.7 -f RANDOM_WITH_FAULTS   -d /path/to/data   -s org.apache.commons.lang org.apache.commons.text   -t 5 -r -l
```

### Rec‑Weighted (SFL⁺)

```bash
java -jar target/diagnosis-1.0.0-jar-with-dependencies.jar   run sfl+   -p <project> -b <bug>   -P 0.7 -f RANDOM_WITH_FAULTS   -C PROBABILITIES   -R 1000 -W RANDOM_INFORMED   -d /path/to/data   -s org.apache.commons.lang org.apache.commons.text   -t 5 -r -l
```

- `-R`: number of random walks.
- `-W`: walk strategy (`RANDOM`, `RANDOM_INFORMED`, etc.).
- `-C`: computation strategy (`PROBABILITIES` or `PROBABILITIES_HIT`).

### Matching and Execution Matching

```bash
java -jar target/diagnosis-1.0.0-jar-with-dependencies.jar   run match   -p <project> -b <bug>   -P 0.7 -f RANDOM   -d /path/to/data   -s org.apache.commons.lang
```

### Ground Truth Queries

```bash
java -jar target/diagnosis-1.0.0-jar-with-dependencies.jar   run query ground-truth   -p <project> -b <bug>   -P 0.7 -d /path/to/data   -s org.apache.commons.lang   -l
```

---

## Replicability Notes

1. Organize your dataset as described above.
2. Compile with the provided `pom.xml` (or equivalent Gradle).
3. Use the same percentages, walk settings, and iteration counts as in the paper.
4. To fix random seeds, use the `-i` and `-t` options.

---

## Citation

If you use this replication package, please cite our paper as indicated in the manuscript. A BibTeX entry will be provided upon publication.

---

## License

This replication package is released under the MIT License. See the `LICENSE` file for details.
