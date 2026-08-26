import React, { useEffect, useMemo, useState } from 'react'
import { API_URL, api } from './api'

const emptyTask = {
  title: '',
  description: '',
  status: 'TODO',
  priority: 'MEDIUM',
  dueDate: ''
}

function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="provider-icon">
      <path fill="#4285F4" d="M21.6 12.23c0-.71-.06-1.4-.18-2.06H12v3.9h5.38a4.6 4.6 0 0 1-2 3.02v2.53h3.24c1.9-1.75 2.98-4.32 2.98-7.39Z" />
      <path fill="#34A853" d="M12 22c2.7 0 4.97-.9 6.62-2.38l-3.24-2.53c-.9.6-2.05.96-3.38.96-2.6 0-4.8-1.76-5.59-4.13H3.06v2.6A10 10 0 0 0 12 22Z" />
      <path fill="#FBBC05" d="M6.41 13.92A6 6 0 0 1 6.1 12c0-.67.11-1.32.31-1.92v-2.6H3.06A10 10 0 0 0 2 12c0 1.62.39 3.16 1.06 4.52l3.35-2.6Z" />
      <path fill="#EA4335" d="M12 5.95c1.47 0 2.79.5 3.83 1.5l2.87-2.87A9.63 9.63 0 0 0 12 2a10 10 0 0 0-8.94 5.48l3.35 2.6C7.2 7.71 9.4 5.95 12 5.95Z" />
    </svg>
  )
}

function GithubIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="provider-icon github-icon">
      <path fill="currentColor" d="M12 2C6.48 2 2 6.58 2 12.23c0 4.52 2.87 8.35 6.84 9.71.5.1.68-.22.68-.49 0-.24-.01-1.04-.01-1.88-2.78.62-3.37-1.2-3.37-1.2-.46-1.19-1.11-1.5-1.11-1.5-.91-.64.07-.62.07-.62 1 .07 1.53 1.06 1.53 1.06.9 1.56 2.34 1.11 2.91.85.09-.66.35-1.11.64-1.37-2.22-.26-4.56-1.14-4.56-5.06 0-1.12.39-2.03 1.03-2.75-.1-.26-.45-1.31.1-2.72 0 0 .84-.28 2.75 1.05A9.35 9.35 0 0 1 12 6.97c.85 0 1.7.12 2.5.35 1.91-1.33 2.75-1.05 2.75-1.05.55 1.41.2 2.46.1 2.72.64.72 1.03 1.63 1.03 2.75 0 3.93-2.35 4.8-4.58 5.06.36.32.68.94.68 1.9 0 1.37-.01 2.47-.01 2.81 0 .27.18.59.69.49A10.25 10.25 0 0 0 22 12.23C22 6.58 17.52 2 12 2Z" />
    </svg>
  )
}

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('cloudtask_token'))
  const [mode, setMode] = useState('login')
  const [auth, setAuth] = useState({ name: '', email: '', password: '' })
  const [remember, setRemember] = useState(true)
  const [showPassword, setShowPassword] = useState(false)
  const [authTheme, setAuthTheme] = useState('light')
  const [tasks, setTasks] = useState([])
  const [task, setTask] = useState(emptyTask)
  const [editingId, setEditingId] = useState(null)
  const [filter, setFilter] = useState('')
  const [message, setMessage] = useState('')

  async function loadTasks() {
    if (!token) return
    try {
      const suffix = filter ? `?status=${filter}` : ''
      setTasks(await api(`/api/v1/tasks${suffix}`))
    } catch (e) {
      setMessage(e.message)
    }
  }

  useEffect(() => {
    const hash = new URLSearchParams(window.location.hash.replace(/^#/, ''))
    const oauthToken = hash.get('token')
    const oauthName = hash.get('name')
    const oauthError = hash.get('error')

    if (oauthToken) {
      localStorage.setItem('cloudtask_token', oauthToken)
      if (oauthName) localStorage.setItem('cloudtask_name', oauthName)
      setToken(oauthToken)
      setMessage(oauthName ? `Bem-vindo, ${oauthName}.` : 'Login social concluído.')
      window.history.replaceState({}, '', '/')
      return
    }

    if (oauthError) {
      setMessage(decodeURIComponent(oauthError))
      window.history.replaceState({}, '', '/')
    }
  }, [])

  useEffect(() => {
    loadTasks()
  }, [token, filter])

  async function submitAuth(e) {
    e.preventDefault()
    setMessage('')
    try {
      const path = mode === 'login' ? '/api/v1/auth/login' : '/api/v1/auth/register'
      const payload = mode === 'login'
        ? { email: auth.email, password: auth.password }
        : auth

      const result = await api(path, {
        method: 'POST',
        body: JSON.stringify(payload)
      })

      localStorage.setItem('cloudtask_token', result.token)
      localStorage.setItem('cloudtask_name', result.name)
      setToken(result.token)
      setMessage(`Bem-vindo, ${result.name}.`)
    } catch (e) {
      setMessage(e.message)
    }
  }

  function startSocialLogin(provider) {
    window.location.assign(`${API_URL}/oauth2/authorization/${provider}`)
  }

  async function saveTask(e) {
    e.preventDefault()
    setMessage('')
    try {
      const payload = { ...task, dueDate: task.dueDate || null }
      if (editingId) {
        await api(`/api/v1/tasks/${editingId}`, {
          method: 'PUT',
          body: JSON.stringify(payload)
        })
      } else {
        await api('/api/v1/tasks', {
          method: 'POST',
          body: JSON.stringify(payload)
        })
      }
      setTask(emptyTask)
      setEditingId(null)
      await loadTasks()
    } catch (e) {
      setMessage(e.message)
    }
  }

  async function removeTask(id) {
    if (!confirm('Excluir esta tarefa?')) return
    try {
      await api(`/api/v1/tasks/${id}`, { method: 'DELETE' })
      await loadTasks()
    } catch (e) {
      setMessage(e.message)
    }
  }

  function editTask(item) {
    setEditingId(item.id)
    setTask({
      title: item.title,
      description: item.description || '',
      status: item.status,
      priority: item.priority,
      dueDate: item.dueDate || ''
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function logout() {
    localStorage.removeItem('cloudtask_token')
    localStorage.removeItem('cloudtask_name')
    setToken(null)
    setTasks([])
  }

  const stats = useMemo(() => ({
    total: tasks.length,
    done: tasks.filter(t => t.status === 'DONE').length,
    critical: tasks.filter(t => t.priority === 'CRITICAL').length
  }), [tasks])

  if (!token) {
    return (
      <main className={`auth-experience ${authTheme}`}>
        <section className="auth-hero" aria-label="Benefícios do CloudTask Enterprise">
          <div className="hero-brand">
            <div className="brand-mark">☁</div>
            <div>
              <strong>CloudTask</strong>
              <span>Enterprise</span>
            </div>
          </div>

          <div className="hero-copy">
            <span className="eyebrow">CLOUD-NATIVE TASK MANAGEMENT</span>
            <h1>Gestão inteligente de tarefas para equipes produtivas</h1>
            <p>Organize, priorize e acompanhe seu trabalho com segurança, velocidade e observabilidade.</p>

            <div className="feature-list">
              <div><span>✓</span><div><b>Organização centralizada</b><small>Projetos, tarefas, prioridades e prazos em um só lugar.</small></div></div>
              <div><span>✓</span><div><b>Produtividade com foco</b><small>Fluxos simples para manter o time alinhado e eficiente.</small></div></div>
              <div><span>✓</span><div><b>Segurança moderna</b><small>JWT, OAuth 2.0 e boas práticas de autenticação.</small></div></div>
              <div><span>✓</span><div><b>Observabilidade</b><small>Micrometer, Prometheus e Grafana integrados.</small></div></div>
            </div>
          </div>

          <div className="hero-illustration" aria-hidden="true">
            <div className="mini-card card-a"><b>92%</b><span>concluído</span></div>
            <div className="mini-card card-b"><i>✓</i><span>Deploy seguro</span></div>
            <div className="mini-card card-c"><span>▥ ▥ ▥</span><b>Cloud Ready</b></div>
          </div>

          <p className="hero-footer">© 2026 CloudTask Enterprise</p>
        </section>

        <section className="auth-panel">
          <button
            type="button"
            className="theme-toggle"
            aria-label="Alternar tema"
            onClick={() => setAuthTheme(authTheme === 'light' ? 'dark' : 'light')}
          >
            {authTheme === 'light' ? '☾' : '☀'}
          </button>

          <div className="auth-form-wrap">
            <div className="mobile-brand">
              <div className="brand-mark">☁</div>
              <strong>CloudTask <span>Enterprise</span></strong>
            </div>

            <div className="auth-heading">
              <h2>{mode === 'login' ? 'Bem-vindo de volta' : 'Crie sua conta'}</h2>
              <p>{mode === 'login' ? 'Entre para continuar no CloudTask Enterprise.' : 'Comece a organizar seu trabalho em poucos segundos.'}</p>
            </div>

            <form onSubmit={submitAuth} className="auth-form">
              {mode === 'register' && (
                <label>
                  <span>Nome</span>
                  <div className="input-shell">
                    <span className="input-icon">◉</span>
                    <input
                      placeholder="Seu nome"
                      value={auth.name}
                      onChange={e => setAuth({ ...auth, name: e.target.value })}
                      required
                    />
                  </div>
                </label>
              )}

              <label>
                <span>E-mail</span>
                <div className="input-shell">
                  <span className="input-icon">✉</span>
                  <input
                    type="email"
                    placeholder="seuemail@empresa.com"
                    value={auth.email}
                    onChange={e => setAuth({ ...auth, email: e.target.value })}
                    required
                  />
                </div>
              </label>

              <label>
                <span>Senha</span>
                <div className="input-shell">
                  <span className="input-icon">⌁</span>
                  <input
                    type={showPassword ? 'text' : 'password'}
                    placeholder="••••••••"
                    value={auth.password}
                    onChange={e => setAuth({ ...auth, password: e.target.value })}
                    required
                  />
                  <button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)} aria-label="Mostrar ou ocultar senha">
                    {showPassword ? '◉' : '◎'}
                  </button>
                </div>
              </label>

              {mode === 'login' && (
                <div className="auth-options">
                  <label className="remember-option">
                    <input type="checkbox" checked={remember} onChange={e => setRemember(e.target.checked)} />
                    <span>Lembrar-me</span>
                  </label>
                  <button type="button" className="text-action" onClick={() => setMessage('Recuperação de senha será adicionada em uma próxima etapa.')}>Esqueceu sua senha?</button>
                </div>
              )}

              <button type="submit" className="primary-auth-button">
                {mode === 'login' ? 'Entrar' : 'Criar conta'}
              </button>
            </form>

            <div className="auth-divider"><span>ou continue com</span></div>

            <div className="social-grid">
              <button type="button" className="social-button" onClick={() => startSocialLogin('google')}>
                <GoogleIcon /> Google
              </button>
              <button type="button" className="social-button" onClick={() => startSocialLogin('github')}>
                <GithubIcon /> GitHub
              </button>
            </div>

            <p className="mode-switch">
              {mode === 'login' ? 'Ainda não tem uma conta?' : 'Já possui uma conta?'}{' '}
              <button type="button" onClick={() => {
                setMode(mode === 'login' ? 'register' : 'login')
                setMessage('')
              }}>
                {mode === 'login' ? 'Cadastre-se grátis' : 'Entrar'}
              </button>
            </p>

            {message && <div className="auth-message" role="status">{message}</div>}

            <p className="auth-terms">Ao continuar, você concorda com os <span>Termos de Uso</span> e a <span>Política de Privacidade</span>.</p>
          </div>
        </section>
      </main>
    )
  }

  return (
    <main className="app-shell">
      <header>
        <div>
          <div className="brand">☁️ CloudTask <span>Enterprise</span></div>
          <p className="muted">Cloud-native Task Management Platform</p>
        </div>
        <button className="secondary" onClick={logout}>Sair</button>
      </header>

      <section className="stats">
        <article><b>{stats.total}</b><span>Tarefas</span></article>
        <article><b>{stats.done}</b><span>Concluídas</span></article>
        <article><b>{stats.critical}</b><span>Críticas</span></article>
      </section>

      <section className="grid">
        <article className="panel">
          <h2>{editingId ? 'Editar tarefa' : 'Nova tarefa'}</h2>
          <form onSubmit={saveTask}>
            <input
              placeholder="Título"
              value={task.title}
              onChange={e => setTask({ ...task, title: e.target.value })}
              required
            />
            <textarea
              placeholder="Descrição"
              rows="5"
              value={task.description}
              onChange={e => setTask({ ...task, description: e.target.value })}
            />
            <div className="row">
              <select value={task.status} onChange={e => setTask({ ...task, status: e.target.value })}>
                <option value="TODO">A fazer</option>
                <option value="IN_PROGRESS">Em andamento</option>
                <option value="DONE">Concluída</option>
              </select>
              <select value={task.priority} onChange={e => setTask({ ...task, priority: e.target.value })}>
                <option value="LOW">Baixa</option>
                <option value="MEDIUM">Média</option>
                <option value="HIGH">Alta</option>
                <option value="CRITICAL">Crítica</option>
              </select>
            </div>
            <input
              type="date"
              value={task.dueDate}
              onChange={e => setTask({ ...task, dueDate: e.target.value })}
            />
            <button>{editingId ? 'Salvar alterações' : 'Adicionar tarefa'}</button>
            {editingId && (
              <button type="button" className="secondary" onClick={() => {
                setEditingId(null)
                setTask(emptyTask)
              }}>Cancelar</button>
            )}
          </form>
          {message && <div className="message">{message}</div>}
        </article>

        <article className="panel">
          <div className="list-head">
            <h2>Minhas tarefas</h2>
            <select value={filter} onChange={e => setFilter(e.target.value)}>
              <option value="">Todos</option>
              <option value="TODO">A fazer</option>
              <option value="IN_PROGRESS">Em andamento</option>
              <option value="DONE">Concluídas</option>
            </select>
          </div>

          <div className="task-list">
            {tasks.length === 0 && <p className="muted">Nenhuma tarefa encontrada.</p>}
            {tasks.map(item => (
              <div className="task-card" key={item.id}>
                <div className="task-top">
                  <h3>{item.title}</h3>
                  <span className={`badge ${item.priority.toLowerCase()}`}>{item.priority}</span>
                </div>
                <p>{item.description || 'Sem descrição'}</p>
                <small>{item.status} {item.dueDate ? `• Prazo: ${item.dueDate}` : ''}</small>
                <div className="actions">
                  <button className="secondary" onClick={() => editTask(item)}>Editar</button>
                  <button className="danger" onClick={() => removeTask(item.id)}>Excluir</button>
                </div>
              </div>
            ))}
          </div>
        </article>
      </section>
    </main>
  )
}
