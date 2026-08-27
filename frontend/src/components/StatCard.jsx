import React from 'react'

export default function StatCard({ label, value, hint, icon, tone = 'blue' }) {
  return (
    <article className={`dashboard-stat tone-${tone}`}>
      <div className="stat-icon" aria-hidden="true">{icon}</div>
      <div className="stat-copy">
        <span>{label}</span>
        <strong>{value}</strong>
        <small>{hint}</small>
      </div>
    </article>
  )
}
