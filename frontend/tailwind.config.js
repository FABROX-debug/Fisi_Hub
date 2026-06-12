/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#1E1B4B',
        accent: '#6D28D9',
        accentLight: '#8B5CF6',
        surface: '#F8F7FF',
        card: '#FFFFFF',
        border: '#E5E7EB',
        textPrimary: '#111827',
        textMuted: '#6B7280',
        success: '#10B981',
        warning: '#F59E0B',
        danger: '#EF4444',
        info: '#3B82F6',
      },
      boxShadow: {
        card: '0 1px 3px rgba(15, 23, 42, 0.08)',
        glow: '0 0 20px rgba(109, 40, 217, 0.25)',
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'monospace'],
      },
      keyframes: {
        shimmer: {
          '0%': { transform: 'translateX(-100%)' },
          '100%': { transform: 'translateX(100%)' },
        },
      },
      animation: {
        shimmer: 'shimmer 1.8s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}
