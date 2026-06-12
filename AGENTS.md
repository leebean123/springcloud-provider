# AGENTS.md

Instructions for agentic coding agents working in this repository.

## Code Editing Guidelines

### Common Rules

Required: When creating or modifying any file in this section, add or update file modification marker, and follow these rules:

- **Must use the system's current date and time** — always retrieve the exact timestamp via system command, do not hardcode or guess the time
- If a marker already exists, update it to the current system time
- **The marker should be placed at the end of the file**, as the last line of content (skip trailing empty lines). This avoids conflicts with special file headers (shebang, doctype, XML declaration, pragma, YAML frontmatter, etc.).
- **When a file already has an AI marker**, any new code must be added **above** the marker line to ensure the marker always remains the last line of content.

### Backend Files Modification Marker

| File type | Comment format |
|-----------|---------------|
| `.java` | `// Modified by AI on YYYY-MM-DD HH:MM:SS` |
| `.py` | `# Modified by AI on YYYY-MM-DD HH:MM:SS` |

### Frontend Files Modification Marker

| File type | Comment format |
|-----------|---------------|
| `.vue` | `<!-- Modified by AI on YYYY-MM-DD HH:MM:SS -->` |
| `.js/.mjs/.cjs/.ts/.jsx/.tsx` | `// Modified by AI on YYYY-MM-DD HH:MM:SS` |
| `.css/.scss/.less` | `/* Modified by AI on YYYY-MM-DD HH:MM:SS */` |
| `.html` | `<!-- Modified by AI on YYYY-MM-DD HH:MM:SS -->` |

### Other Files

| File type     | Comment format |
|---------------|---------------|
| `.xml`        | `<!-- Modified by AI on YYYY-MM-DD HH:MM:SS -->` |
| `.yaml/.yml`  | `# Modified by AI on YYYY-MM-DD HH:MM:SS` |
| `.properties` | `# Modified by AI on YYYY-MM-DD HH:MM:SS` |
| `.md`         | `<!-- Modified by AI on YYYY-MM-DD HH:MM:SS -->` |

### Unsupported Files

- `.json`: JSON does not support comments. Do not insert a marker line into JSON files.
- For file types not mentioned above, do not add modification marker.
