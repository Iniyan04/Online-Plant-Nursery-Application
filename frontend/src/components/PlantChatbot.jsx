import { useEffect, useRef, useState } from 'react'

const GROQ_API_KEY = import.meta.env.VITE_GROQ_API_KEY
const GROQ_URL = 'https://api.groq.com/openai/v1/chat/completions'

const SUGGESTIONS = [
  'How often to water?',
  'Is it safe for pets?',
  'How much sunlight?',
  'Best pot size?',
  'Common problems?'
]

function buildSystemPrompt(plant) {
  return `You are a friendly and knowledgeable plant care assistant for Greenroot Nursery.
The customer is currently viewing this plant:

Name: ${plant.commonName}
Type: ${plant.typeOfPlant || 'Plant'}
Difficulty: ${plant.difficultyLevel || 'Not specified'}
Height: ${plant.plantHeight} cm
Spread: ${plant.plantSpread || 'Not specified'}
Bloom Time: ${plant.bloomTime || 'Not specified'}
Ideal Temperature: ${plant.temparature || 'Not specified'}
Medicinal / Culinary Use: ${plant.medicinalOrCulinaryUse || 'None'}
Description: ${plant.plantDescription || 'Not specified'}
Price: ₹${plant.plantCost}
Stock: ${plant.plantsStock} available

Answer questions specifically and helpfully about this plant.
Keep answers concise - 2 to 4 sentences max unless the customer asks for more detail.
If asked something unrelated to plants or gardening, politely redirect back to plant care topics.
Always be warm, encouraging, and helpful.`
}

async function askGroq(systemPrompt, history, userMessage) {
  const messages = [
    { role: 'system', content: systemPrompt },
    ...history.map((msg) => ({
      role: msg.role === 'user' ? 'user' : 'assistant',
      content: msg.text
    })),
    { role: 'user', content: userMessage }
  ]

  const res = await fetch(GROQ_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${GROQ_API_KEY}`
    },
    body: JSON.stringify({
      model: 'llama-3.3-70b-versatile',
      messages,
      max_tokens: 300,
      temperature: 0.7
    })
  })

  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err?.error?.message || `Groq API error ${res.status}`)
  }

  const data = await res.json()
  return data.choices?.[0]?.message?.content || "Sorry, I couldn't generate a response."
}

export default function PlantChatbot({ plant }) {
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [showSuggestions, setShowSuggestions] = useState(true)
  const messagesEndRef = useRef(null)
  const inputRef = useRef(null)

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, loading])

  useEffect(() => {
    if (open) {
      setTimeout(() => inputRef.current?.focus(), 100)
    }
  }, [open])

  useEffect(() => {
    if (open && messages.length === 0) {
      setMessages([{
        role: 'bot',
        text: `Hi! I'm your plant care assistant for ${plant.commonName}. Ask me anything - watering schedule, sunlight needs, pet safety, or anything else about this plant!`
      }])
    }
  }, [open])

  async function sendMessage(text) {
    const userText = text.trim()
    if (!userText || loading) return

    setShowSuggestions(false)
    setError('')
    setInput('')

    const newMessages = [...messages, { role: 'user', text: userText }]
    setMessages(newMessages)
    setLoading(true)

    try {
      if (!GROQ_API_KEY) {
        throw new Error('Groq API key not configured. Add VITE_GROQ_API_KEY to your .env file.')
      }

      const systemPrompt = buildSystemPrompt(plant)
      const history = newMessages.slice(1)
      const botReply = await askGroq(systemPrompt, history.slice(0, -1), userText)

      setMessages((prev) => [...prev, { role: 'bot', text: botReply }])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      sendMessage(input)
    }
  }

  return (
    <>
      <button
        className="chatbot-fab"
        onClick={() => setOpen((o) => !o)}
        aria-label="Plant care assistant"
        title="Ask about this plant"
      >
        {open ? 'x' : '🌿'}
      </button>

      {open && (
        <div className="chatbot-window">
          <div className="chatbot-header">
            <div className="chatbot-header-left">
              <div className="chatbot-avatar">🌿</div>
              <div className="chatbot-header-text">
                <strong>Plant Care Assistant</strong>
                <span>Ask me about {plant.commonName}</span>
              </div>
            </div>
            <button className="chatbot-close" onClick={() => setOpen(false)} aria-label="Close">
              x
            </button>
          </div>

          <div className="chatbot-messages">
            {messages.map((msg, i) => (
              <div key={i} className={`chat-msg ${msg.role}`}>
                <div className="chat-bubble">
                  {msg.text.replace(/\*\*(.*?)\*\*/g, '$1')}
                </div>
              </div>
            ))}

            {loading && (
              <div className="chat-msg bot">
                <div className="typing-dots">
                  <span /><span /><span />
                </div>
              </div>
            )}

            {error && (
              <div className="chat-msg bot">
                <div className="chat-bubble" style={{ color: 'var(--dianthus)', fontSize: '0.8rem' }}>
                  {error}
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {showSuggestions && (
            <div className="chatbot-suggestions">
              {SUGGESTIONS.map((s) => (
                <button key={s} className="suggestion-chip" onClick={() => sendMessage(s)}>
                  {s}
                </button>
              ))}
            </div>
          )}

          <div className="chatbot-input-bar">
            <textarea
              ref={inputRef}
              className="chatbot-input"
              rows={1}
              placeholder="Ask about watering, sunlight, pests..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={loading}
            />
            <button
              className="chatbot-send"
              onClick={() => sendMessage(input)}
              disabled={!input.trim() || loading}
              aria-label="Send"
            >
              &gt;
            </button>
          </div>
        </div>
      )}
    </>
  )
}