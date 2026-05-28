# Bob AI Agent Android 🤖

Bob is an Android-native AI agent framework built with Kotlin and Jetpack Compose. It explores how large language models can interact with real Android device capabilities through structured tool calling, memory, confirmation flows, and multi-step reasoning.

This project is both a functional prototype and a security research project exploring the risks of AI agents on mobile devices, especially when apps are granted sensitive permissions such as SMS, contacts, and internet access.

---

# Core Purpose

Bob explores the boundary between a helpful AI assistant and a potentially risky autonomous mobile agent.

The app demonstrates how an LLM-powered Android agent can:

- Search SMS messages
- Read message content
- Look up contacts
- Prepare SMS actions
- Send SMS after confirmation
- Chain tools together
- Use previous tool results as context
- Make structured decisions using JSON
- Require user confirmation before sensitive actions

The project also highlights why mobile AI agents require strong safety boundaries, permission controls, and transparent user consent.

---

# Security Research Focus

This project specifically investigates the security implications of giving AI agents access to sensitive Android permissions.

A key concern is that once an app is granted permissions such as:

- `READ_SMS`
- `SEND_SMS`
- `READ_CONTACTS`
- internet access

an AI-driven agent could theoretically be misused to inspect private user data, identify sensitive messages, or automate risky workflows.

For example, a malicious or poorly controlled agent with SMS access could attempt to:

- scan SMS messages
- monitor incoming texts
- detect OTP or verification codes
- identify account recovery messages
- link messages to contacts/accounts
- send SMS messages without clear user understanding
- combine SMS access with internet access to exfiltrate sensitive data

Bob does **not** implement background OTP harvesting, hidden surveillance, credential theft, or silent exfiltration. Instead, the project is designed to explore these risks responsibly and demonstrate safer architecture patterns.

---

# Why SMS Permissions Are Sensitive

SMS permissions are especially high risk because text messages may contain:

- one-time passwords
- banking alerts
- account recovery links
- personal conversations
- delivery codes
- private contact information
- authentication messages

An AI agent with unrestricted SMS read/send access could become dangerous if it were allowed to operate silently or continuously in the background.

This is why Bob uses confirmation-based action flows and visible user-triggered interactions.

---

# Safety Design Principles

Bob is designed around the following safety principles:

## 1. Human-in-the-loop confirmation

Sensitive actions should not be executed directly by the LLM.

For example:

```text
User request
→ LLM proposes send_sms
→ App stores pending action
→ User taps Confirm
→ Android sends SMS
```

The LLM can suggest an action, but the Android app controls whether that action is allowed to execute.

---

## 2. No silent SMS sending

Bob does not silently send messages in the background.

Sending SMS is treated as a high-risk action and should require explicit user confirmation.

---

## 3. No background OTP monitoring

Bob is not designed to continuously monitor SMS messages for OTPs, verification codes, or authentication messages.

The project intentionally avoids building stealth background monitoring behaviour.

---

## 4. Visible tool execution

The app is designed so user-triggered workflows are visible in the chat interface.

The goal is transparency, not hidden automation.

---

## 5. Scoped tools

Bob uses a tool registry where Android capabilities are exposed as controlled tools.

Examples:

| Tool | Description | Confirmation Required |
|---|---|---|
| `search_sms` | Searches SMS messages | No |
| `find_contact` | Looks up contacts | No |
| `send_sms` | Sends SMS messages | Yes |
| `echo_tool` | Debug/testing tool | No |

---

# Agent Architecture

Bob is not just a chatbot. It uses an agent runtime architecture:

```text
User Input
→ LLM Decision
→ Tool Call
→ Android Tool Execution
→ Tool Result
→ Further LLM Reasoning
→ Final Response or Confirmation
```

This allows Bob to perform multi-step workflows such as:

```text
User: Send Bobby a message saying hello

1. LLM decides to use find_contact
2. Android searches contacts
3. Contact number is returned
4. LLM creates a send_sms confirmation request
5. User confirms
6. Android sends the SMS
```

---

# Implemented Features

## SMS Search

Bob can search SMS messages using natural language.

Example:

```text
"What time is dinner?"
```

Bob can search SMS content, retrieve relevant messages, and answer based on the result.

---

## Contact Lookup

Bob can search Android contacts by name.

Example:

```text
"Find Bobby"
```

---

## SMS Sending

Bob can prepare and send SMS messages after explicit confirmation.

Example:

```text
"Send Bobby a message saying I'm on my way"
```

Flow:

```text
find_contact
→ requires_confirmation(send_sms)
→ user confirms
→ SMS sent
```

---

# Current Tool System

Implemented tools:

| Tool | Description |
|---|---|
| `search_sms` | Searches SMS messages |
| `find_contact` | Searches Android contacts |
| `send_sms` | Sends SMS messages after confirmation |
| `echo_tool` | Testing/debug tool |

---

# Tech Stack

- Kotlin
- Jetpack Compose
- Android ViewModel
- Coroutines
- Retrofit
- OkHttp
- OpenAI Responses API
- Material 3

---

# Permissions Used

This project currently uses:

```xml
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
```

These permissions are used for research and explicitly user-triggered workflows.

For production apps, direct SMS permissions should be treated with caution and may be restricted by Google Play policies.

---

# OpenAI API Key Setup

This project does **not** include an OpenAI API key.

You must provide your own key locally.

Create a `local.properties` file in the project root:

```properties
OPENAI_API_KEY=your_api_key_here
```

Do not commit this file.

The included `.gitignore` should exclude:

```gitignore
local.properties
```

---

# Important Production Warning

This project currently calls OpenAI directly from the Android app.

That is acceptable for local development and experimentation, but it is **not recommended for production**.

A production-ready version should use:

```text
Android App
→ Your Backend
→ OpenAI API
```

rather than:

```text
Android App
→ OpenAI API directly
```

This is because API keys embedded in Android apps can potentially be extracted from APKs.

---

# Google Play / Policy Considerations

This project is experimental.

Apps using SMS permissions may be subject to strict review and restrictions on Google Play.

A production version should consider:

- avoiding direct `SEND_SMS`
- using SMS intents where possible
- clearly explaining permission usage
- avoiding background SMS monitoring
- avoiding OTP scanning
- requiring explicit user confirmation for sensitive actions
- maintaining clear audit logs of agent actions

---

# Current Limitations

- Direct OpenAI API usage from Android
- Limited tool chaining depth
- No backend orchestration yet
- No long-term vector memory
- No streaming responses yet
- No audit log UI yet
- No production-grade permission manager yet

---

# Future Research Areas

- Safer mobile agent sandboxes
- Tool permission scoring
- Runtime action auditing
- Prompt injection against tool-using agents
- Malicious SDK interaction with AI agents
- Background automation risks
- OTP and authentication-message abuse scenarios
- Safer alternatives to direct SMS permissions
- Backend-based policy enforcement
- Human-in-the-loop agent governance

---

# Disclaimer

This project is intended for:

- educational purposes
- Android AI agent experimentation
- mobile AI security research
- safe exploration of tool-using agents

It is **not** intended for:

- credential harvesting
- OTP theft
- hidden surveillance
- unauthorized device access
- silent SMS monitoring
- malicious automation
- spyware behaviour

The purpose of this project is to better understand the risks and design safer AI agent systems.
