import React, { useEffect, useMemo, useState } from 'react'
import { API_URL, api } from './api'
import Sidebar from './components/Sidebar'
import StatCard from './components/StatCard'
import TaskForm from './components/TaskForm'
import TaskList from './components/TaskList'

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

function isOverdue(task) {
  if (!task.dueDate || task.status === 'DONE') return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return new Date(`${task.dueDate}T00:00:00`) < today
}

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('cloudtask_token'))
  const [mode, setMode] = useState('login')
  const [auth, setAuth] = useState({ name: '', email: '', password: '' })
  const [remember, setRemember] = useState(true)
  const [showPassword, setShowPassword] = useState(false)
  const [authTheme, setAuthTheme] = useState('light')
  const [appTheme, setAppTheme] = useState(localStorage.getItem('cloudtask_theme') || 'dark')
  const [tasks, setTasks] = useState([])
  const [task, setTask] = useState(emptyTask)
  const [editingId, setEditingId] = useState(null)
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [priorityFilter, setPriorityFilter] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const userName = localStorage.getItem('cloudtask_name') || 'Usuário'

  async function loadTasks() {
    if (!token) return
    setLoading(true)
    try {
      setTasks(await api('/api/v1/tasks'))
    } catch (e) {
      setMessage(e.message)
    } finally {
      setLoading(false)
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
  }, [token])

  useEffect(() => {
    if (!message || !token) return undefined
    const timer = window.setTimeout(() => setMessage(''), 4200)
    return () => window.clearTimeout(timer)
  }, [message, token])

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
        setMessage('Tarefa atualizada com sucesso.')
      } else {
        await api('/api/v1/tasks', {
          method: 'POST',
          body: JSON.stringify(payload)
        })
        setMessage('Tarefa criada com sucesso.')
      }
      setTask(emptyTask)
      setEditingId(null)
      await loadTasks()
    } catch (e) {
      setMessage(e.message)
    }
  }

  async function removeTask(id) {
    if (!confirm('Excluir esta tarefa? Esta ação não poderá ser desfeita.')) return
    try {
      await api(`/api/v1/tasks/${id}`, { method: 'DELETE' })
      setMessage('Tarefa excluída.')
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
    window.setTimeout(() => document.getElementById('task-editor')?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 0)
  }

  function newTask() {
    setEditingId(null)
    setTask(emptyTask)
    window.setTimeout(() => document.getElementById('task-editor')?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 0)
  }

  function cancelEdit() {
    setEditingId(null)
    setTask(emptyTask)
  }

  function logout() {
    localStorage.removeItem('cloudtask_token')
    localStorage.removeItem('cloudtask_name')
    setToken(null)
    setTasks([])
    setMessage('')
  }

  function toggleAppTheme() {
    const nextTheme = appTheme === 'dark' ? 'light' : 'dark'
    localStorage.setItem('cloudtask_theme', nextTheme)
    setAppTheme(nextTheme)
  }

  const stats = useMemo(() => ({
    total: tasks.length,
    todo: tasks.filter(item => item.status === 'TODO').length,
    inProgress: tasks.filter(item => item.status === 'IN_PROGRESS').length,
    done: tasks.filter(item => item.status === 'DONE').length,
    overdue: tasks.filter(isOverdue).length
  }), [tasks])

  const filteredTasks = useMemo(() => {
    const term = search.trim().toLowerCase()
    return tasks.filter(item => {
      const matchesSearch = !term || `${item.title} ${item.description || ''}`.toLowerCase().includes(term)
      const matchesStatus = !statusFilter || item.status === statusFilter
      const matchesPriority = !priorityFilter || item.priority === priorityFilter
      return matchesSearch && matchesStatus && matchesPriority
    })
  }, [tasks, search, statusFilter, priorityFilter])

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
    <div className={`enterprise-app ${appTheme}`}>
      <Sidebar userName={userName} onNewTask={newTask} onLogout={logout} />

      <main className="dashboard-main">
        <header className="dashboard-topbar">
          <div>
            <span className="topbar-eyebrow">CLOUDTASK WORKSPACE</span>
            <h1>Olá, {userName.split(' ')[0]} 👋</h1>
            <p>Acompanhe prioridades, prazos e evolução do seu trabalho em um só lugar.</p>
          </div>

          <div className="topbar-actions">
            <button className="theme-action" type="button" onClick={toggleAppTheme} aria-label="Alternar tema do dashboard">
              {appTheme === 'dark' ? '☀' : '☾'}
            </button>
            <button className="primary-action" type="button" onClick={newTask}>+ Nova tarefa</button>
          </div>
        </header>

        <section className="runtime-banner" id="overview">
          <div>
            <span className="runtime-pill"><i /> AWS RUNTIME ONLINE</span>
            <h2>Operação cloud-native com visão executiva</h2>
            <p>Frontend React e backend Spring Boot operando em ECS/Fargate, com RDS PostgreSQL, ALB, ECR, Secrets Manager e CloudWatch.</p>
          </div>
          <div className="runtime-metrics">
            <div><strong>ECS</strong><span>Fargate</span></div>
            <div><strong>RDS</strong><span>PostgreSQL</span></div>
            <div><strong>ALB</strong><span>Healthy</span></div>
          </div>
        </section>

        <section className="dashboard-stats" aria-label="Resumo das tarefas">
          <StatCard label="Total" value={stats.total} hint="tarefas cadastradas" icon="▦" tone="blue" />
          <StatCard label="A fazer" value={stats.todo} hint="aguardando execução" icon="○" tone="slate" />
          <StatCard label="Em andamento" value={stats.inProgress} hint="trabalho em execução" icon="◔" tone="amber" />
          <StatCard label="Concluídas" value={stats.done} hint="entregas finalizadas" icon="✓" tone="green" />
          <StatCard label="Atrasadas" value={stats.overdue} hint="requerem atenção" icon="!" tone="red" />
        </section>

        <section className="workspace-grid">
          <TaskList
            tasks={filteredTasks}
            loading={loading}
            search={search}
            setSearch={setSearch}
            statusFilter={statusFilter}
            setStatusFilter={setStatusFilter}
            priorityFilter={priorityFilter}
            setPriorityFilter={setPriorityFilter}
            onEdit={editTask}
            onDelete={removeTask}
            onNew={newTask}
          />

          <TaskForm
            task={task}
            setTask={setTask}
            editingId={editingId}
            onSubmit={saveTask}
            onCancel={cancelEdit}
          />
        </section>

        <footer className="dashboard-footer">
          <span>CloudTask Enterprise · Professional Portfolio</span>
          <span>Java 21 · Spring Boot · React · AWS · Terraform · CI/CD</span>
        </footer>

        {message && <div className="dashboard-toast" role="status">✓ {message}</div>}
      </main>
    </div>
  )
}
