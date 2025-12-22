# How to Use the Architecture Documentation

This guide explains how to use the architecture documentation files to create visual diagrams using AI tools like Gemini.

## Documentation Files Overview

### 1. ARCHITECTURE.md
**Purpose**: Main system architecture document  
**Best for**: Understanding the overall system design and getting a comprehensive overview  
**Use when**: You need to understand the full architecture, technology choices, or development guidelines

### 2. ARCHITECTURE_COMPONENTS.md
**Purpose**: Detailed descriptions of individual components  
**Best for**: Understanding specific components, their responsibilities, and interactions  
**Use when**: You need to create detailed component diagrams or understand data flows

### 3. ARCHITECTURE_DIAGRAMS.md
**Purpose**: Structured descriptions optimized for visualization tools  
**Best for**: Creating actual diagrams using AI tools like Gemini, Mermaid, or PlantUML  
**Use when**: You want to generate visual architectural diagrams

## How to Create Diagrams with Gemini

### Method 1: Generate Layer Diagram
1. Copy the "Overall System Architecture - 3-Layer View" section from ARCHITECTURE_DIAGRAMS.md
2. Paste it into Gemini with this prompt:
   ```
   Please create a layered architecture diagram based on this description:
   [paste the layer description here]
   ```

### Method 2: Generate Flow Diagrams
1. Copy any flow section (e.g., "Translation Request Sequence") from ARCHITECTURE_DIAGRAMS.md
2. Use this prompt:
   ```
   Create a sequence diagram showing this flow:
   [paste the flow description here]
   ```

### Method 3: Generate Component Diagrams
1. Copy component descriptions from ARCHITECTURE_COMPONENTS.md
2. Use this prompt:
   ```
   Create a component diagram showing these components and their connections:
   [paste the component descriptions here]
   ```

## Recommended Diagram Types

### 1. High-Level Architecture (Start Here)
**Source**: ARCHITECTURE_DIAGRAMS.md → "Overall System Architecture - 3-Layer View"  
**Diagram Type**: Layered architecture diagram  
**Shows**: Presentation, Domain, and Data layers with main components

### 2. Translation Flow
**Source**: ARCHITECTURE_DIAGRAMS.md → "Translation Request Sequence"  
**Diagram Type**: Sequence diagram or flowchart  
**Shows**: Step-by-step flow from user input to displaying translation

### 3. Component Interactions
**Source**: ARCHITECTURE_COMPONENTS.md → "Component Descriptions by Layer"  
**Diagram Type**: Component diagram  
**Shows**: All major components and their relationships

### 4. Data Architecture
**Source**: ARCHITECTURE_DIAGRAMS.md → "Database Architecture"  
**Diagram Type**: Entity-relationship diagram  
**Shows**: Database schema and relationships

### 5. Network Architecture
**Source**: ARCHITECTURE_DIAGRAMS.md → "Network Architecture Diagram"  
**Diagram Type**: Network diagram  
**Shows**: API communication and offline handling

### 6. Deployment Architecture
**Source**: ARCHITECTURE_DIAGRAMS.md → "Deployment Architecture"  
**Diagram Type**: Deployment diagram  
**Shows**: Build pipeline and runtime architecture

## Sample Gemini Prompts

### For a Complete System Overview
```
Based on the following architecture description, create a comprehensive system architecture diagram for an Android translation app. Use a layered architecture approach showing:
- Presentation Layer (UI components)
- Domain Layer (business logic)
- Data Layer (repositories and data sources)

Include connections between layers and external services.

[Paste content from ARCHITECTURE_DIAGRAMS.md - Overall System Architecture section]
```

### For a Detailed Component Diagram
```
Create a detailed component diagram for an Android app showing:
- All components listed below
- Their relationships and dependencies
- Data flow between components
- Use boxes for components and arrows for relationships

[Paste content from ARCHITECTURE_COMPONENTS.md - Component Descriptions section]
```

### For a Sequence Diagram
```
Create a sequence diagram showing the translation request flow in an Android app:
- Show all steps from user input to displaying the result
- Include error handling paths
- Show interactions between components

[Paste content from ARCHITECTURE_DIAGRAMS.md - Translation Request Sequence]
```

### For a Database Schema
```
Create a database schema diagram showing:
- Tables and their columns
- Primary keys
- Relationships between tables
- Data types

[Paste content from ARCHITECTURE_DIAGRAMS.md - Database Schema]
```

## Tips for Best Results

1. **Be Specific**: Tell Gemini exactly what type of diagram you want (sequence, component, layer, etc.)

2. **Start Simple**: Begin with high-level diagrams before creating detailed ones

3. **Iterate**: If the first result isn't perfect, refine your prompt with more details

4. **Combine Sections**: You can combine multiple sections from the documentation for more comprehensive diagrams

5. **Request Different Views**: Ask for multiple diagram types (top-down, left-right, circular) to see what works best

6. **Add Context**: Mention it's for an Android mobile app to get more relevant visualizations

## Example Workflow

1. **Start with Overview**
   - Use ARCHITECTURE.md to understand the system
   - Create a high-level 3-layer diagram from ARCHITECTURE_DIAGRAMS.md

2. **Add Detail**
   - Use ARCHITECTURE_COMPONENTS.md to understand individual components
   - Create component diagrams for each layer

3. **Show Flows**
   - Use the flow sections in ARCHITECTURE_DIAGRAMS.md
   - Create sequence diagrams for key user journeys

4. **Technical Details**
   - Create database schema diagrams
   - Create network architecture diagrams
   - Create dependency injection graphs

## Exporting Diagrams

After generating diagrams with Gemini:
- Save as PNG/SVG for presentations
- Export to draw.io format for editing
- Convert to Mermaid/PlantUML syntax for version control
- Include in technical documentation

## Common Diagram Requests

### For Stakeholders
- High-level architecture (3 layers)
- User flow diagrams
- Deployment architecture

### For Developers
- Detailed component diagrams
- Class relationship diagrams
- Dependency injection graphs
- Database schemas

### For DevOps
- Deployment pipeline
- CI/CD flow
- Infrastructure architecture

### For Security Review
- Data flow diagrams
- Network security architecture
- Authentication flow

## Updating Diagrams

As the project evolves:
1. Update the markdown documentation files
2. Regenerate diagrams using the same prompts
3. Keep prompt templates for consistency
4. Version control the prompts alongside the documentation

## Need Help?

If you need specific diagrams:
1. Identify which section of the documentation is most relevant
2. Choose the appropriate diagram type
3. Copy the relevant section
4. Use one of the sample prompts above
5. Iterate with Gemini until you get the desired result

## Additional Resources

- **Mermaid.js**: For text-based diagrams in markdown
- **PlantUML**: For more complex UML diagrams
- **Draw.io**: For manual diagram creation
- **Excalidraw**: For hand-drawn style diagrams

---

These architecture documents provide a solid foundation for creating comprehensive visual representations of the Hmong Translate Android App system architecture.
