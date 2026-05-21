// A reusable card that displays one bug's summary.
// Used in BugList and Dashboard pages.

import { useNavigate } from 'react-router-dom';

// Maps status values to Tailwind color classes
// bg-* = background color, text-* = text color
const STATUS_STYLES = {
  OPEN:        'bg-red-100 text-red-800',
  IN_PROGRESS: 'bg-yellow-100 text-yellow-800',
  RESOLVED:    'bg-green-100 text-green-800',
  CLOSED:      'bg-gray-100 text-gray-600',
};

const PRIORITY_STYLES = {
  LOW:      'bg-blue-100 text-blue-800',
  MEDIUM:   'bg-indigo-100 text-indigo-800',
  HIGH:     'bg-orange-100 text-orange-800',
  CRITICAL: 'bg-red-100 text-red-800',
};

// bug = the bug object from the API
// onStatusChange = optional callback when status is updated (from parent)
export default function BugCard({ bug, onStatusChange }) {
  const navigate = useNavigate();

  return (
    // The whole card is clickable — navigates to the bug detail page
    <div
      onClick={() => navigate(`/bugs/${bug.id}`)}
      className="bg-white border border-gray-200 rounded-lg p-4 cursor-pointer
                 hover:shadow-md hover:border-indigo-300 transition-all"
    >
      {/* Top row: title + status badge */}
      <div className="flex items-start justify-between gap-2 mb-2">
        <h3 className="font-medium text-gray-900 text-sm leading-snug">
          {bug.title}
        </h3>
        {/* Status badge — color changes based on status */}
        <span className={`text-xs px-2 py-0.5 rounded-full font-medium shrink-0
                          ${STATUS_STYLES[bug.status] || 'bg-gray-100 text-gray-600'}`}>
          {/* Replace underscore with space: IN_PROGRESS → IN PROGRESS */}
          {bug.status.replace('_', ' ')}
        </span>
      </div>

      {/* Description preview — only show first 80 characters */}
      {bug.description && (
        <p className="text-xs text-gray-500 mb-3 line-clamp-2">
          {/* line-clamp-2 = Tailwind utility to truncate to 2 lines */}
          {bug.description}
        </p>
      )}

      {/* Bottom row: priority + project + assignee */}
      <div className="flex items-center gap-2 flex-wrap">
        {/* Priority badge */}
        <span className={`text-xs px-2 py-0.5 rounded-full font-medium
                          ${PRIORITY_STYLES[bug.priority] || 'bg-gray-100'}`}>
          {bug.priority}
        </span>

        {/* Project name */}
        <span className="text-xs text-gray-400">
          {bug.projectName}
        </span>

        {/* Assigned to — only if assigned */}
        {bug.assignedToUsername && (
          <span className="text-xs text-gray-400 ml-auto">
            → {bug.assignedToUsername}
          </span>
        )}
      </div>
    </div>
  );
}