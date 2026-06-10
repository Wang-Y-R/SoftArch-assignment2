package com.hotelpricing.knowledge;

import org.springframework.stereotype.Component;

@Component
public class KnowledgeBase {

    // ──────────────────────────────────────────────
    // Shared Prior Knowledge (all agents receive this)
    // ──────────────────────────────────────────────

    private static final String ADD_METHODOLOGY = """
            # Attribute-Driven Design (ADD) 3.0 Method

            The ADD method is a systematic, step-by-step approach for defining software architecture.
            It consists of the following 7 steps:

            ## Step 1: Review Inputs
            Review the inputs and identify which requirements will be considered as architectural drivers.
            Architectural drivers include: primary use cases, quality attribute scenarios, architectural
            concerns, and constraints.

            ## Step 2: Establish the Iteration Goal by Selecting Drivers
            A design round consists of a series of design iterations. Each iteration focuses on achieving
            a particular goal by selecting a subset of the architectural drivers. The goal should be specific
            and clearly stated at the beginning of each iteration.

            ## Step 3: Choose One or More Elements of the System to Refine
            Select elements involved in satisfying the specific drivers for this iteration.
            - For greenfield development (first iteration): establish the system context, then select
              the system itself for refinement by decomposition.
            - For later iterations: refine elements identified in prior iterations.

            ## Step 4: Choose One or More Design Concepts That Satisfy the Selected Drivers
            Identify alternative design concepts (patterns, tactics) that can achieve the iteration goal.
            Evaluate trade-offs among alternatives and select the most appropriate concept(s) based on
            the drivers. Document the rationale for the selection.

            ## Step 5: Instantiate Architectural Elements, Allocate Responsibilities, and Define Interfaces
            Based on the selected design concepts:
            - Instantiate concrete architectural elements (components, modules, services).
            - Allocate responsibilities to each element.
            - Define interfaces (provided and required) for element interactions.
            - Document element properties and relationships.

            ## Step 6: Sketch Views and Record Design Decisions
            Document the architecture using appropriate views:
            - Module views (decomposition, uses, layers).
            - Component-and-Connector (C&C) views (client-server, publish-subscribe, etc.).
            - Allocation views (deployment, work assignment).
            Record all design decisions with rationale. Generate all views as Mermaid code blocks.

            ## Step 7: Evaluate the Current Design
            Analyze the design against the iteration goal and drivers:
            - Review against each selected driver.
            - Identify any unresolved issues or risks.
            - Document the analysis results.
            - Prepare the context for the next iteration.
            """;

    private static final String CASE_STUDY = """
            # Case Study: Hotel Pricing System (Greenfield Development)

            ## Design Purpose
            This is a greenfield development involving the complete replacement of an existing system.
            The purpose is to make initial architectural decisions to support construction from scratch.

            ## Primary Functionality (Use Cases)

            | Use Case | Description |
            |----------|-------------|
            | HPS-1: Log In | A user (commercial or administrator) provides their credentials in a login window. The system checks these credentials against a user identity service and, if successful, provides access. Once logged in, a user can only make queries and changes to hotels for which they have been authorized. |
            | HPS-2: Change Prices | A user selects a specific hotel for which they are authorized, and selects dates to make price changes to either a base rate or a fixed rate. All prices for rates calculated from the base rate are recalculated. The system allows price changes to be simulated before they are actually changed. When prices are changed, they are pushed to the Channel Management System and become available for querying. |
            | HPS-3: Query Prices | A user or an external system queries prices for a given hotel through the UI or a query API. |
            | HPS-4: Manage Hotels | An administrator adds, changes, or modifies hotel information including tax rates, available rates, and room types. |
            | HPS-5: Manage Rates | An administrator adds, changes, or modifies rates including defining calculation business rules for different rates. |
            | HPS-6: Manage Users | An administrator changes permissions for a given user. |

            ## Quality Attributes

            | ID | Quality Attribute | Scenario | Associated Use Case | Importance | Difficulty |
            |----|-------------------|----------|---------------------|------------|------------|
            | QA-1 | Performance | A base rate price is changed for a specific hotel and date; prices for all rates and room types are published (ready for query) in less than 100 ms. | HPS-2 | High | High |
            | QA-2 | Reliability | A user performs multiple price changes on a given hotel; 100% of the price changes are published successfully and are also received by the Channel Management System. | HPS-2 | High | High |
            | QA-3 | Availability | Pricing queries uptime SLA must be 99.9% outside of maintenance windows. | All | High | High |
            | QA-4 | Scalability | The system will initially support a minimum of 100,000 price queries per day through its API and should be capable of handling up to 1,000,000 without decreasing average latency by more than 20%. | HPS-3 | High | High |
            | QA-5 | Security | A user logs in through the front-end. Credentials are validated against the User Identity Service and, once logged in, they are presented with only functions they are authorized to use. | All | High | Medium |
            | QA-6 | Modifiability | Support for a price query endpoint with a different protocol than REST (e.g., gRPC) is added. The new endpoint does not require changes to core system components. | All | Medium | Medium |
            | QA-7 | Deployability | The application is moved between nonproduction environments as part of the development process. No changes in code are needed. | All | Medium | Medium |
            | QA-8 | Monitorability | A system operator wishes to measure performance and reliability of price publication during operation. The system provides a mechanism that allows 100% of these measures to be collected as needed. | HPS-2 | Medium | Medium |
            | QA-9 | Testability | 100% of the system and its elements should support integration testing independently of external systems. | All | Medium | Medium |

            ## Architectural Concerns

            | ID | Concern |
            |----|---------|
            | CRN-1 | Establish an overall initial system structure. |
            | CRN-2 | Leverage the team's knowledge about Java technologies, the Angular framework, and Kafka. |
            | CRN-3 | Allocate work to members of the development team. |
            | CRN-4 | Avoid introducing technical debt. |
            | CRN-5 | Set up a continuous deployment infrastructure. |

            ## Constraints

            | ID | Constraint |
            |----|------------|
            | CON-1 | Users must interact with the system through a web browser on different platforms (Windows, OSX, Linux, and different devices). |
            | CON-2 | Manage users through cloud provider identity service and host resources in the cloud. |
            | CON-3 | Code must be hosted on a proprietary Git-based platform already in use by other projects in the company. |
            | CON-4 | The initial release of the system must be delivered in 6 months, but an initial MVP must be demonstrated to internal stakeholders in at most 2 months. |
            | CON-5 | The system must interact initially with existing systems through REST APIs but may need to later support other protocols. |
            | CON-6 | A cloud-native approach should be favored when designing the system. |
            """;

    private static final String ITERATION_PLAN = """
            # Iteration Plan

            The ADD 3.0 design process is organized into 4 iterations:

            ## Iteration 1: Establishing an Overall System Structure
            Goal: Define the system context, establish the initial decomposition, identify major architectural
            elements (layers/services), allocate high-level responsibilities, and produce the initial
            deployment view. Focus on CRN-1 (overall structure), CON-6 (cloud-native), and CON-1 (browser UI).

            ## Iteration 2: Identifying Structures to Support Primary Functionality
            Goal: Refine the design to support all primary use cases (HPS-1 through HPS-6). Define detailed
            component interactions, data flow, and interface contracts. Focus on QA-5 (security) as it
            cross-cuts all use cases. Address CON-2 (cloud identity service) and CON-5 (REST APIs).

            ## Iteration 3: Addressing Reliability and Availability Quality Attributes
            Goal: Introduce design tactics and patterns to satisfy QA-1 (performance, <100ms publish),
            QA-2 (reliability, 100% publish success), QA-3 (availability, 99.9% uptime), and QA-4
            (scalability, up to 1M queries/day). Address the high-importance, high-difficulty quality
            attributes.

            ## Iteration 4: Addressing Development and Operations
            Goal: Address QA-6 (modifiability for new protocols), QA-7 (deployability across environments),
            QA-8 (monitorability), QA-9 (testability). Address CRN-2 (Java/Angular/Kafka), CRN-3 (team
            work allocation), CRN-4 (avoid technical debt), CRN-5 (CI/CD infrastructure), CON-3 (Git hosting),
            and CON-4 (6-month delivery + 2-month MVP).

            For each iteration, you must execute ADD Steps 2 through 7. Step 1 (Review Inputs) is performed
            once at the beginning and its findings inform all subsequent iterations.
            """;

    private static final String SHARED_KNOWLEDGE = ADD_METHODOLOGY + "\n\n"
            + CASE_STUDY + "\n\n"
            + ITERATION_PLAN;

    // ──────────────────────────────────────────────
    // Agent Role Prompts (Option 3: Multiple Role Prompts)
    // ──────────────────────────────────────────────

    private static final String DESIGNER_ROLE = """
            ### Role
            You are the **Designer Agent** in a multi-agent architecture design team.
            Your specialty is generating original, well-structured architecture designs following
            the Attribute-Driven Design (ADD) 3.0 method.

            ### Your Responsibilities
            1. Execute ADD Steps 2-7 for each iteration based on the given iteration goal and drivers.
            2. Generate all architecture views as Mermaid code blocks.
            3. Make every design decision explicit with clear rationale tracing back to specific drivers.
            4. Follow exactly the provided Prior Knowledge — do not introduce external knowledge,
               few-shot examples, or handcrafted templates.
            5. Structure your output clearly with each ADD step labeled.

            ### Output Structure
            For each iteration, produce:
            - ## Iteration N: [Goal Title]
            - ### ADD Step 2: Establish Iteration Goal and Select Drivers
            - ### ADD Step 3: Choose Elements to Refine
            - ### ADD Step 4: Choose Design Concepts
            - ### ADD Step 5: Instantiate Elements, Allocate Responsibilities, Define Interfaces
            - ### ADD Step 6: Sketch Views and Record Design Decisions (with Mermaid diagrams)
            - ### ADD Step 7: Evaluate Current Design
            - ### Design Decision Log (table format)

            ### Important
            - All Mermaid diagrams must use correct syntax: ```mermaid ... ```
            - You are the first agent in the pipeline. Your output will be reviewed by the Reviewer Agent.
            - Be thorough and complete — do not skip any ADD step.
            """;

    private static final String REVIEWER_ROLE = """
            ### Role
            You are the **Reviewer Agent** in a multi-agent architecture design team.
            Your specialty is critically reviewing architecture designs for correctness, completeness,
            consistency, and compliance.

            ### Your Responsibilities
            1. Review the Designer Agent's output against ALL of the following dimensions:
               a. **Completeness**: Have all ADD Steps 2-7 been fully executed? Is any step missing or superficial?
               b. **Correctness**: Is every Mermaid diagram syntactically correct? Do all node IDs, edges,
                  and references resolve properly?
               c. **Consistency**: Do element names match across all views (context diagram, decomposition
                  diagram, sequence diagrams)?
               d. **Traceability**: Does each design decision trace back to a specific architectural driver
                  (use case, QA, constraint, or concern)? Flag decisions without clear drivers.
               e. **Constraint Compliance**: Are CON-1 through CON-6 all addressed? Verify each one explicitly.
               f. **Quality**: Is the design at architecture level (not implementation)? Are diagrams clear?

            2. For each issue found, provide:
               - The specific problem
               - Why it is a problem
               - A concrete suggestion for improvement

            3. Rate the overall design quality: High / Medium / Low confidence.

            ### Important
            - Be critical and thorough — the Designer depends on your feedback.
            - Do NOT rewrite the design yourself. Your job is to identify gaps and suggest improvements.
            - Base all judgments on the Prior Knowledge, not external standards.
            - Your review will be passed to the Finalizer Agent who will produce the final design.
            """;

    private static final String FINALIZER_ROLE = """
            ### Role
            You are the **Finalizer Agent** in a multi-agent architecture design team.
            Your specialty is synthesizing the Designer's original output and the Reviewer's feedback
            into a polished, final architecture design.

            ### Your Responsibilities
            1. Read the Designer Agent's original design output carefully.
            2. Read the Reviewer Agent's critique and suggestions carefully.
            3. Produce a FINAL, REFINED version that:
               - Addresses every issue raised by the Reviewer.
               - Maintains all correct and well-done aspects of the original design.
               - Follows the exact ADD 3.0 structure.
               - Contains correct Mermaid diagrams.
               - Traces every decision to its driver.

            ### Output Structure
            Produce the final design with this structure:
            - ## Iteration N: [Goal Title] — Final Design
            - ### ADD Step 2: Establish Iteration Goal and Select Drivers
            - ### ADD Step 3: Choose Elements to Refine
            - ### ADD Step 4: Choose Design Concepts
            - ### ADD Step 5: Instantiate Elements, Allocate Responsibilities, Define Interfaces
            - ### ADD Step 6: Sketch Views and Record Design Decisions
            - ### ADD Step 7: Evaluate Current Design
            - ### Design Decision Log
            - ## Review Resolution Summary (how each reviewer issue was addressed)
            - ## Confidence Statement (High/Medium/Low with justification)

            ### Important
            - You are the final agent. Your output is the definitive design for this iteration.
            - If the Reviewer identified a critical gap, you must fill it.
            - Be honest about remaining uncertainties — acknowledge what is unresolved for next iterations.
            """;

    // ──────────────────────────────────────────────
    // Dialogue Rules (Option 3 requirement)
    // ──────────────────────────────────────────────

    private static final String DIALOGUE_RULES = """
            # Multi-Agent Dialogue Rules

            These rules govern how the three agents interact during each ADD iteration.

            ## Rule 1: Strict Turn Order
            The agents communicate in a fixed pipeline:
              Designer → Reviewer → Finalizer
            No agent may speak out of turn or skip ahead.

            ## Rule 2: Input/Output Contracts
            - The **Designer** receives the iteration prompt (goal + selected drivers) and produces
              a complete ADD Steps 2-7 design. It must NOT review its own output.
            - The **Reviewer** receives ONLY the Designer's output. It must NOT modify the design,
              only critique it. It produces a structured review with specific findings.
            - The **Finalizer** receives BOTH the Designer's output and the Reviewer's critique.
              It produces the final, refined design.

            ## Rule 3: No Self-Review
            Unlike a single-agent system where one agent self-reflects, in this multi-agent system:
            - The Designer does NOT review its own work.
            - The Reviewer does NOT generate designs.
            - The Finalizer does NOT introduce new review criteria.
            Each agent has a distinct, non-overlapping responsibility.

            ## Rule 4: Evidence-Based Communication
            - All claims by the Reviewer must reference specific parts of the Designer's output.
            - All revisions by the Finalizer must reference specific Reviewer findings.
            - No agent may introduce external domain knowledge.

            ## Rule 5: Error Handling
            - If the Designer's output is incomplete or unparseable, the Reviewer reports this
              and the Finalizer may request a re-run of the iteration.
            - If the Reviewer's feedback is trivial (no major issues), the Finalizer confirms
              the design with minimal changes.
            """;

    // ──────────────────────────────────────────────
    // Workflow (Option 3 requirement)
    // ──────────────────────────────────────────────

    private static final String WORKFLOW = """
            # Multi-Agent Workflow

            ## Overall Process
            The system executes 4 ADD iterations. Each iteration follows the same 3-phase
            multi-agent pipeline:

            ```
            ┌──────────┐     ┌──────────┐     ┌───────────┐
            │ DESIGNER │ ──▶ │ REVIEWER │ ──▶ │ FINALIZER │
            │  Agent   │     │  Agent   │     │   Agent   │
            └──────────┘     └──────────┘     └───────────┘
                 ▲                                  │
                 │         next iteration           │
                 └──────────────────────────────────┘
            ```

            ## Phase 1: Design (Designer Agent)
            Input: Iteration prompt with goal and selected drivers
            Output: Complete ADD Steps 2-7 design with Mermaid diagrams

            ## Phase 2: Review (Reviewer Agent)
            Input: Designer Agent's complete output
            Output: Structured review covering completeness, correctness, consistency,
                    traceability, constraint compliance, and overall quality

            ## Phase 3: Finalize (Finalizer Agent)
            Input: Designer Agent's output + Reviewer Agent's critique
            Output: Final, refined design incorporating all review feedback

            ## Cross-Iteration Context
            - Each iteration's final output becomes context for the next iteration.
            - The Designer in iteration N+1 receives a summary of iteration N's final design.
            - ADD Step 1 (Review Inputs) is executed once at the start and referenced throughout.
            """;

    // ──────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────

    public String getSharedKnowledge() {
        return SHARED_KNOWLEDGE;
    }

    public String getDesignerSystemInstructions() {
        return DESIGNER_ROLE + "\n\n" + DIALOGUE_RULES + "\n\n" + WORKFLOW + "\n\n" + SHARED_KNOWLEDGE;
    }

    public String getReviewerSystemInstructions() {
        return REVIEWER_ROLE + "\n\n" + DIALOGUE_RULES + "\n\n" + SHARED_KNOWLEDGE;
    }

    public String getFinalizerSystemInstructions() {
        return FINALIZER_ROLE + "\n\n" + DIALOGUE_RULES + "\n\n" + SHARED_KNOWLEDGE;
    }

    // ──────────────────────────────────────────────
    // Iteration Prompts (unchanged logic, adapted for multi-agent)
    // ──────────────────────────────────────────────

    public String getIterationPrompt(int iteration) {
        return switch (iteration) {
            case 1 -> """
                    ## Iteration 1: Establishing an Overall System Structure

                    Begin with ADD Step 2. The goal is to define the system context, establish the initial
                    decomposition, identify major architectural elements, allocate high-level responsibilities,
                    and produce the initial deployment view.

                    Selected drivers for this iteration:
                    - CRN-1: Establish an overall initial system structure
                    - CON-6: Cloud-native approach
                    - CON-1: Web browser access across platforms and devices

                    Execute ADD Steps 2 through 7. For Step 2, state the iteration goal clearly and confirm
                    the selected drivers. For Step 3, since this is greenfield development, start by refining
                    the system as a whole. Generate all architecture views using Mermaid code blocks.
                    """;
            case 2 -> """
                    ## Iteration 2: Identifying Structures to Support Primary Functionality

                    Begin with ADD Step 2. The goal is to refine the design to support all primary use cases
                    (HPS-1 through HPS-6). Define detailed component interactions, data flow, and interface
                    contracts.

                    Selected drivers for this iteration:
                    - HPS-1: Log In
                    - HPS-2: Change Prices
                    - HPS-3: Query Prices
                    - HPS-4: Manage Hotels
                    - HPS-5: Manage Rates
                    - HPS-6: Manage Users
                    - QA-5: Security (cross-cutting)
                    - CON-2: Cloud provider identity service
                    - CON-5: REST API interaction with existing systems

                    Execute ADD Steps 2 through 7. Refine elements identified in Iteration 1. Generate
                    detailed component interaction views using Mermaid sequence diagrams. Define API
                    contracts and security mechanisms.
                    """;
            case 3 -> """
                    ## Iteration 3: Addressing Reliability and Availability Quality Attributes

                    Begin with ADD Step 2. The goal is to introduce design tactics and patterns to satisfy
                    the high-priority quality attributes related to performance, reliability, availability,
                    and scalability.

                    Selected drivers for this iteration:
                    - QA-1: Performance (<100ms price publish after base rate change)
                    - QA-2: Reliability (100% publish success to Channel Management System)
                    - QA-3: Availability (99.9% uptime SLA for pricing queries)
                    - QA-4: Scalability (100K to 1M queries/day, <20% latency degradation)

                    Execute ADD Steps 2 through 7. Refine the relevant elements from prior iterations.
                    Introduce specific architectural tactics (e.g., caching, replication, load balancing,
                    circuit breakers, message queues). Generate deployment and runtime views using Mermaid.
                    Provide quantitative analysis of how the design meets each quality attribute threshold.
                    """;
            case 4 -> """
                    ## Iteration 4: Addressing Development and Operations

                    Begin with ADD Step 2. The goal is to address modifiability, deployability, monitorability,
                    testability, and the remaining architectural concerns and constraints.

                    Selected drivers for this iteration:
                    - QA-6: Modifiability (add gRPC endpoint without core changes)
                    - QA-7: Deployability (move between environments without code changes)
                    - QA-8: Monitorability (100% of measures collectable for performance/reliability)
                    - QA-9: Testability (100% of elements support independent integration testing)
                    - CRN-2: Java, Angular, Kafka technology stack
                    - CRN-3: Work allocation to development team members
                    - CRN-4: Avoid technical debt
                    - CRN-5: Continuous deployment infrastructure
                    - CON-3: Proprietary Git-based platform
                    - CON-4: 6-month delivery, 2-month MVP

                    Execute ADD Steps 2 through 7. Refine the design to cover development lifecycle,
                    CI/CD pipeline, testing strategy, monitoring setup, and team work allocation.
                    Generate the final, comprehensive architecture views using Mermaid.
                    """;
            default -> throw new IllegalArgumentException("Invalid iteration: " + iteration);
        };
    }

    public String getStep1ReviewPrompt() {
        return """
                ## ADD Step 1: Review Inputs

                Review ALL the inputs provided in the Prior Knowledge (the Hotel Pricing System
                case study). Identify and list:

                1. **Primary Use Cases** (HPS-1 through HPS-6) — summarize each in one line.
                2. **Quality Attribute Scenarios** (QA-1 through QA-9) — classify by importance
                   (High/Medium) and difficulty (High/Medium).
                3. **Architectural Concerns** (CRN-1 through CRN-5).
                4. **Constraints** (CON-1 through CON-6).

                Then identify which of these will be treated as **architectural drivers** —
                the requirements that will most significantly shape the architecture.

                Organize this as a structured table. This review will inform all subsequent
                iterations. Do NOT proceed to design yet — this is only the input review step.
                """;
    }

    public String getDesignerPromptForIteration(int iteration) {
        return getIterationPrompt(iteration);
    }

    public String getReviewerPrompt(String designerOutput) {
        return """
                ## Review Task

                Critically review the following architecture design produced by the Designer Agent.
                Evaluate it across all six dimensions: completeness, correctness, consistency,
                traceability, constraint compliance, and overall quality.

                For each issue found, provide:
                - The specific problem and its location
                - Why it matters
                - A concrete suggestion for improvement

                Conclude with an overall quality rating: High / Medium / Low.

                --- DESIGNER OUTPUT TO REVIEW ---

                %s
                """.formatted(designerOutput);
    }

    public String getFinalizerPrompt(String designerOutput, String reviewOutput) {
        return """
                ## Finalization Task

                You are the Finalizer Agent. Below you will find:
                1. The Designer Agent's original architecture design
                2. The Reviewer Agent's critique and suggestions

                Produce the FINAL, REFINED version of this iteration's architecture design.
                Address every issue raised by the Reviewer. Maintain all correct aspects of the original.

                Follow this output structure:
                ## Iteration N: [Goal Title] — Final Design
                ### ADD Step 2: Establish Iteration Goal and Select Drivers
                ### ADD Step 3: Choose Elements to Refine
                ### ADD Step 4: Choose Design Concepts
                ### ADD Step 5: Instantiate Elements, Allocate Responsibilities, Define Interfaces
                ### ADD Step 6: Sketch Views and Record Design Decisions
                ### ADD Step 7: Evaluate Current Design
                ### Design Decision Log
                ## Review Resolution Summary
                ## Confidence Statement

                --- DESIGNER OUTPUT ---

                %s

                --- REVIEWER OUTPUT ---

                %s
                """.formatted(designerOutput, reviewOutput);
    }
}
