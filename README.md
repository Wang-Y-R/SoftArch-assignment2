# Hotel Pricing System — Multi-Agent Architecture Design

Software Architecture Assignment 2 (2026). Uses ADD 3.0 method with a **multi-agent** pipeline (Distributed reasoning + collaborative verification) to design the Hotel Pricing System architecture.

**LLM:** DeepSeek-V4-Flash (via Spring AI Alibaba / DashScope)  
**Group Paradigm:** Option 3 — Multi-Agent (Designer → Reviewer → Finalizer)

## Project Structure

```
src/main/java/com/hotelpricing/
├── Application.java                  # Spring Boot entry point
├── agent/
│   ├── DesignerAgent.java            # Phase 1: generates initial ADD 3.0 design
│   ├── ReviewerAgent.java            # Phase 2: reviews across 6 dimensions
│   └── FinalizerAgent.java           # Phase 3: synthesizes final design
├── config/
│   └── AgentConfig.java              # Wires 3 ChatClients with distinct system prompts
├── knowledge/
│   └── KnowledgeBase.java            # Prior knowledge + 3 role prompts + dialogue rules + workflow
├── log/
│   └── ConversationLogger.java       # Records multi-agent conversations with timestamps
└── runner/
    └── ArchitectureDesignRunner.java # Orchestrates 4 ADD iterations
```

## Multi-Agent Pipeline

```
Designer ──→ Reviewer ──→ Finalizer
  (design)    (critique)    (polish)
```

Each of the 4 ADD iterations runs through all 3 agents sequentially:
- **DesignerAgent** — Produces ADD Steps 2–7 with Mermaid diagrams
- **ReviewerAgent** — Checks completeness, correctness, consistency, traceability, constraint compliance, and quality
- **FinalizerAgent** — Addresses all review findings, produces the final iteration design

## ADD Iteration Plan

| Iteration | Goal | Key Drivers |
|-----------|------|-------------|
| 1 | Establish overall system structure | CRN-1, CON-6, CON-1 |
| 2 | Support primary functionality (6 use cases) | HPS-1~6, QA-5, CON-2, CON-5 |
| 3 | Address reliability & availability QAs | QA-1~4 |
| 4 | Development & operations | QA-6~9, CRN-2~5, CON-3~4 |

## How to Run

**Prerequisites:** Java 21, Maven

```bash
# Set your DashScope API key (optional, has a default)
export DASHSCOPE_API_KEY=sk-xxxxxxxx

# Run the multi-agent design pipeline
mvn spring-boot:run
```

The app starts on port 8081 and runs automatically (via `CommandLineRunner`). All 4 iterations execute sequentially with automatic retry (3 attempts per agent call).

## Configuration

See [application.yml](src/main/resources/application.yml):
- `spring.ai.dashscope.chat.options.model` — currently `deepseek-v4-flash`
- `spring.ai.dashscope.chat.options.temperature` — `0.3`

## Output Logs

After running, complete conversation logs are saved to:
```
src/main/resources/logs/
├── step1-review-log-*.txt       # ADD Step 1 output
├── iteration-1-log-*.txt        # Full 3-agent conversation for Iteration 1
├── iteration-2-log-*.txt        # Full 3-agent conversation for Iteration 2
├── iteration-3-log-*.txt        # Full 3-agent conversation for Iteration 3
├── iteration-4-log-*.txt        # Full 3-agent conversation for Iteration 4
└── summary-report.txt           # Interaction cost analysis
```

## Deliverables Checklist

- [x] Source code (15 points)
- [x] Complete conversation logs with timestamps (15 points)
- [ ] Report (20 points) — see Appendix in assignment PDF

## Notes

- The PDF assignment file and generated logs are gitignored (large binary / generated content)
- Each agent has automatic retry (3 attempts with 5–10s backoff) for API timeout resilience
- System instructions follow Option 3 requirements: Prior knowledge + Multiple role prompts + Dialogue rules + Workflow
