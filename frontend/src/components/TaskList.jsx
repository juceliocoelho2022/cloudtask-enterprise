import React from 'react'

const statusLabel = {
  TODO: 'A fazer',
  IN_PROGRESS: 'Em andamento',
  DONE: 'Concluída'
}

const priorityLabel = {
  LOW: 'Baixa',
  MEDIUM: 'Média',
  HIGH: 'Alta',
  CRITICAL: 'Crítica'
}

function formatDate(value) {
  if (!value) return 'Sem prazo'
  const date = new Date(`${value}T12:00:00`)
  return new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).format(date)
}

function isOverdue(item) {
  if (!item.dueDate || item.status === 'DONE') return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return new Date(`${item.dueDate}T00:00:00`) < today
}

export default function TaskList({
  tasks,
  loading,
  search,
  setSearch,
  statusFilter,
  setStatusFilter,
  priorityFilter,
  setPriorityFilter,
  onEdit,
  onDelete,
  onNew
}) {
  return (
    <section className="workspace-card task-workspace" id="tasks">
      <div className="task-list-heading">
        <div>
          <span className="section-kicker">EXECUÇÃO</span>
          <h2>Minhas tarefas</h2>
          <p>Pesquise, filtre e acompanhe o trabalho em andamento.</p>
        </div>
        <button className="primary-action compact" type="button" onClick={onNew}>+ Nova tarefa</button>
      </div>

      <div className="task-toolbar" aria-label="Filtros de tarefas">
        <div className="search-box">
          <span aria-hidden="true">⌕</span>
          <input
            type="search"
            placeholder="Buscar por título ou descrição..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)} aria-label="Filtrar por status">
          <option value="">Todos os status</option>
          <option value="TODO">A fazer</option>
          <option value="IN_PROGRESS">Em andamento</option>
          <option value="DONE">Concluídas</option>
        </select>
        <select value={priorityFilter} onChange={e => setPriorityFilter(e.target.value)} aria-label="Filtrar por prioridade">
          <option value="">Todas as prioridades</option>
          <option value="LOW">Baixa</option>
          <option value="MEDIUM">Média</option>
          <option value="HIGH">Alta</option>
          <option value="CRITICAL">Crítica</option>
        </select>
      </div>

      <div className="task-results-meta">
        <span>{tasks.length} {tasks.length === 1 ? 'resultado' : 'resultados'}</span>
        {(search || statusFilter || priorityFilter) && <span className="filter-active">Filtros ativos</span>}
      </div>

      <div className="professional-task-list">
        {loading && (
          <div className="loading-state">
            <span className="loading-dot" />
            <span>Carregando tarefas...</span>
          </div>
        )}

        {!loading && tasks.length === 0 && (
          <div className="empty-state">
            <div className="empty-icon">✓</div>
            <h3>Nenhuma tarefa encontrada</h3>
            <p>Ajuste os filtros ou crie uma nova tarefa para começar.</p>
            <button className="ghost-action" type="button" onClick={onNew}>Criar tarefa</button>
          </div>
        )}

        {!loading && tasks.map(item => (
          <article className={`professional-task-card ${isOverdue(item) ? 'is-overdue' : ''}`} key={item.id}>
            <div className="task-card-main">
              <div className="task-card-title-row">
                <div className={`status-indicator status-${item.status.toLowerCase()}`} aria-hidden="true" />
                <div>
                  <h3>{item.title}</h3>
                  <p>{item.description || 'Sem descrição adicionada.'}</p>
                </div>
              </div>

              <div className="task-card-tags">
                <span className={`task-chip status-chip status-${item.status.toLowerCase()}`}>{statusLabel[item.status] || item.status}</span>
                <span className={`task-chip priority-${item.priority.toLowerCase()}`}>{priorityLabel[item.priority] || item.priority}</span>
                <span className={`task-chip due-chip ${isOverdue(item) ? 'overdue-chip' : ''}`}>
                  {isOverdue(item) ? 'Atrasada · ' : 'Prazo · '}{formatDate(item.dueDate)}
                </span>
              </div>
            </div>

            <div className="task-card-actions">
              <button className="icon-action" type="button" onClick={() => onEdit(item)} aria-label={`Editar ${item.title}`}>✎</button>
              <button className="icon-action danger-icon" type="button" onClick={() => onDelete(item.id)} aria-label={`Excluir ${item.title}`}>⌫</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
