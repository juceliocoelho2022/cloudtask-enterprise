import React, { useEffect, useMemo, useState } from 'react'
import { api } from './api'

const emptyTask = {
  title: '',
  description: '',
  status: 'TODO',
  priority: 'MEDIUM',
  dueDate: ''
}

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('cloudtask_token'))
  const [mode, setMode] = useState('login')
  const [auth, setAuth] = useState({ name: '', email: '', password: '' })
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
      <main className="auth-shell">
        <section className="auth-card">
          <div className="brand">☁️ CloudTask <span>Enterprise</span></div>
          <h1>{mode === 'login' ? 'Entrar' : 'Criar conta'}</h1>
          <p className="muted">Java 21 • Spring Boot • React • PostgreSQL • Docker</p>

          <form onSubmit={submitAuth}>
            {mode === 'register' && (
              <input
                placeholder="Nome"
                value={auth.name}
                onChange={e => setAuth({ ...auth, name: e.target.value })}
                required
              />
            )}
            <input
              type="email"
              placeholder="E-mail"
              value={auth.email}
              onChange={e => setAuth({ ...auth, email: e.target.value })}
              required
            />
            <input
              type="password"
              placeholder="Senha"
              value={auth.password}
              onChange={e => setAuth({ ...auth, password: e.target.value })}
              required
            />
            <button>{mode === 'login' ? 'Entrar' : 'Registrar'}</button>
          </form>

          <button className="link-btn" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
            {mode === 'login' ? 'Não tenho conta' : 'Já tenho conta'}
          </button>

          {message && <div className="message">{message}</div>}
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
