import React from 'react'

export default function Sidebar({ userName, onNewTask, onLogout }) {
  const initial = (userName || 'U').trim().charAt(0).toUpperCase()

  return (
    <aside className="enterprise-sidebar">
      <div className="sidebar-brand">
        <div className="sidebar-logo">☁</div>
        <div>
          <strong>CloudTask</strong>
          <span>Enterprise</span>
        </div>
      </div>

      <div className="environment-badge">
        <span className="environment-dot" />
        AWS DEV · ONLINE
      </div>

      <nav className="sidebar-nav" aria-label="Navegação principal">
        <a className="nav-item active" href="#overview"><span>⌂</span>Visão geral</a>
        <a className="nav-item" href="#tasks"><span>☑</span>Minhas tarefas</a>
        <button className="nav-item" type="button" onClick={onNewTask}><span>＋</span>Nova tarefa</button>
        <div className="nav-separator" />
        <span className="nav-caption">PLATAFORMA</span>
        <div className="nav-item disabled"><span>◫</span>Relatórios <small>em breve</small></div>
        <div className="nav-item disabled"><span>⚙</span>Configurações <small>em breve</small></div>
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-user">
          <div className="avatar">{initial}</div>
          <div>
            <strong>{userName || 'Usuário CloudTask'}</strong>
            <span>Sessão autenticada</span>
          </div>
        </div>
        <button className="sidebar-logout" type="button" onClick={onLogout}>Sair</button>
      </div>
    </aside>
  )
}
