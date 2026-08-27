import React from 'react'

export default function TaskForm({ task, setTask, editingId, onSubmit, onCancel }) {
  return (
    <aside className="workspace-card task-editor" id="task-editor">
      <div className="card-heading">
        <div>
          <span className="section-kicker">WORKSPACE</span>
          <h2>{editingId ? 'Editar tarefa' : 'Nova tarefa'}</h2>
          <p>{editingId ? 'Atualize os dados e salve a nova versão.' : 'Transforme uma demanda em uma tarefa rastreável.'}</p>
        </div>
        <span className="editor-badge">{editingId ? 'EDIÇÃO' : 'NOVO'}</span>
      </div>

      <form className="task-form" onSubmit={onSubmit}>
        <label>
          <span>Título</span>
          <input
            placeholder="Ex.: Finalizar pipeline CI/CD"
            value={task.title}
            onChange={e => setTask({ ...task, title: e.target.value })}
            required
          />
        </label>

        <label>
          <span>Descrição</span>
          <textarea
            placeholder="Descreva o objetivo, contexto ou critério de conclusão."
            rows="5"
            value={task.description}
            onChange={e => setTask({ ...task, description: e.target.value })}
          />
        </label>

        <div className="form-grid-2">
          <label>
            <span>Status</span>
            <select value={task.status} onChange={e => setTask({ ...task, status: e.target.value })}>
              <option value="TODO">A fazer</option>
              <option value="IN_PROGRESS">Em andamento</option>
              <option value="DONE">Concluída</option>
            </select>
          </label>

          <label>
            <span>Prioridade</span>
            <select value={task.priority} onChange={e => setTask({ ...task, priority: e.target.value })}>
              <option value="LOW">Baixa</option>
              <option value="MEDIUM">Média</option>
              <option value="HIGH">Alta</option>
              <option value="CRITICAL">Crítica</option>
            </select>
          </label>
        </div>

        <label>
          <span>Prazo</span>
          <input
            type="date"
            value={task.dueDate}
            onChange={e => setTask({ ...task, dueDate: e.target.value })}
          />
        </label>

        <div className="form-actions">
          <button className="primary-action" type="submit">
            {editingId ? 'Salvar alterações' : 'Adicionar tarefa'}
          </button>
          {editingId && (
            <button className="ghost-action" type="button" onClick={onCancel}>Cancelar</button>
          )}
        </div>
      </form>
    </aside>
  )
}
