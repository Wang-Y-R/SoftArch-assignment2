# Hotel Pricing System — Multi-Agent Architecture Design

Software Architecture Assignment 2 (2026). Uses ADD method with a **multi-agent** pipeline (Distributed reasoning + collaborative verification) to design the Hotel Pricing System architecture.

**LLM:** DeepSeek-V4-Flash (via Spring AI Alibaba / DashScope)  
**Group Paradigm:** Option 3 — Multi-Agent (Designer → Reviewer → Finalizer)

## Project Structure

```
src/main/java/com/hotelpricing/
├── Application.java                  # Spring Boot entry point
├── agent/
│   ├── DesignerAgent.java            # Phase 1: generates initial ADD design
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

**Prerequisites:** Java 21, Maven, DashScope API Key

```bash
# 1. Set API key in bash or in application.yml
export DASHSCOPE_API_KEY=sk-xxxxxxxx

# 2. Start the multi-agent pipeline
mvn clean compile spring-boot:run
```

The app starts on port 8081 and all 4 iterations execute automatically. Each agent call has 3 retry attempts on timeout.
A full run takes **~8–12 minutes**.

## Configuration

See [application.yml](src/main/resources/application.yml):
- `spring.ai.dashscope.chat.options.model` — `deepseek-v4-flash`
- `spring.ai.dashscope.chat.options.temperature` — `0.3`
- To switch models, change the value (e.g. `deepseek-v4-pro`)

## Generated Files

After running, the following are produced under `src/main/resources/logs/`:

```
src/main/resources/logs/
├── step1-review-log-*.txt       # ADD Step 1 output
├── iteration-1-log-*.txt        # Iteration 1: Designer + Reviewer + Finalizer
├── iteration-2-log-*.txt        # Iteration 2: Designer + Reviewer + Finalizer
├── iteration-3-log-*.txt        # Iteration 3: Designer + Reviewer + Finalizer
├── iteration-4-log-*.txt        # Iteration 4: Designer + Reviewer + Finalizer
└── summary-report.txt           # Interaction cost analysis (turns, tokens, time)
```

Each iteration log contains all 3 phases tagged with Agent Role, User Prompt, and Agent Response with timestamps.

**Which output to use for the report:** In each iteration log, find the `PHASE 3: FINALIZER AGENT` section labeled `>>> Finalizer Agent Response (Final Output) <<<`. This is the final polished design for that iteration, including all ADD Steps 2–7, Design Decision Log, Review Resolution Summary, and Confidence Statement.

---

## How to Write the Final Report

### Report Structure (from assignment PDF Appendix)

The report has 3 sections. Below is the template and where to find each piece of data.

---

### 一、Output Results of ADD

Use the **Finalizer Agent's output** (Phase 3 in each iteration log) to fill in the ADD steps:

#### 1) Iteration 1: Establishing an Overall System Structure

| Step | Source in log |
|------|--------------|
| ADD Step 2 | Finalizer output → "### ADD Step 2: Establish Iteration Goal and Select Drivers" |
| ADD Step 3 | Finalizer output → "### ADD Step 3: Choose Elements to Refine" |
| ADD Step 4 | Finalizer output → "### ADD Step 4: Choose Design Concepts" |
| ADD Step 5 | Finalizer output → "### ADD Step 5: Instantiate Elements, Allocate Responsibilities, Define Interfaces" |
| ADD Step 6 | Finalizer output → "### ADD Step 6: Sketch Views and Record Design Decisions" |
| ADD Step 7 | Finalizer output → "### ADD Step 7: Perform Analysis of Current Design..." |

#### 2) Iteration 2: Identifying Structures to Support Primary Functionality

Same structure as Iteration 1, but from `iteration-2-log-*.txt` Finalizer output.

#### 3) Iteration 3: Addressing Reliability and Availability Quality Attributes

Same structure, from `iteration-3-log-*.txt` Finalizer output.

#### 4) Iteration 4: Addressing Development and Operations

Same structure, from `iteration-4-log-*.txt` Finalizer output.

> **Tip:** For each iteration, copy the Finalizer's output directly — it already contains all ADD steps in order with tables, Mermaid diagrams, and design decisions.

---

### 二、Interaction Cost Analysis

Fill from `summary-report.txt`:

| Field | Source |
|-------|--------|
| The way of completing the assignment | Multi-Agent (Distributed reasoning + collaborative verification) |
| The LLM used | DeepSeek-V4-Flash (or whatever model is configured) |
| Number of Human Interactions (turns) | From summary: `Number of Human Interactions (turns)` |
| Token Consumption (K tokens) | From summary: `Token Consumption (K tokens)` |
| Time Cost (min) | From summary: `Time Cost (min)` |

Example values from a typical run:
```
Completion Method: Multi-Agent
AI Paradigm: Distributed reasoning + collaborative verification
Agent Pipeline: Designer → Reviewer → Finalizer
LLM Used: DeepSeek-V4-Flash
Number of Human Interactions (turns): 13
Token Consumption (K tokens): 101.1
Time Cost (min): 8~12
```

---

### 三、Individual Reflection

This section is written by each group member individually in English.

#### 1) The problems encountered and the solutions adopted

Describe any issues encountered during the development or execution, such as:
- API timeout issues and how retry mechanism was added (each agent retries 3 times with 5–10s backoff)
- Iteration 2 Finalizer timeout during the first run (DashScope API I/O timeout)
- Mermaid diagram syntax corrections suggested by the Reviewer
- Model choice trade-offs (deepseek-v4-pro vs deepseek-v4-flash)
- Any prompt engineering iterations to get better ADD compliance

#### 2) A detailed account of your personal contributions to the group work

Each member writes their own contribution, for example:

| Name (Chinese) | Contributions |
|----------------|---------------|
| [Name] | [Describe your specific contributions, e.g.: Designed the multi-agent architecture (Designer/Reviewer/Finalizer roles), implemented KnowledgeBase with 3 role prompts + dialogue rules + workflow, implemented retry mechanism in all 3 agents, wrote the report sections...] |

---

## Deliverables Checklist

- [x] Source code (15 points)
- [x] Complete conversation logs with timestamps (15 points) — in `src/main/resources/logs/`
- [ ] Report (20 points) — use the template above, extracting from generated logs

## Notes

- The PDF assignment file and generated logs are gitignored (large binary / generated content)
- Each agent has automatic retry (3 attempts with 5–10s backoff) for API timeout resilience
- System instructions follow Option 3 requirements: Prior knowledge + Multiple role prompts + Dialogue rules + Workflow
