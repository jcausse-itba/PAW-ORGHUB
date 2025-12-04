# CSS Organization

This directory contains the organized CSS files for the web application.

## File Structure

### Main Files
- **`index.css`** - Main CSS file that imports all component styles and contains global styles
- **`components.css`** - Core component styles (buttons, forms, posts, etc.)
- **`header.css`** - Header-specific styles and navigation
- **`footer.css`** - Footer-specific styles and layout

### Usage

To use the CSS in your JSP pages, include the main `index.css` file:

```html
<link rel="stylesheet" href="<c:url value='/css/index.css'/>">
```

## Style Organization

### Global Styles (`index.css`)
- Base HTML element styles
- Utility classes (container, section, hr)
- Responsive breakpoints
- CSS imports

### Component Styles (`components.css`)
- Buttons (`.btn`, `.btn-primary`, `.btn-secondary`)
- Forms (`.input`, `.form-row`, `.form-error`)
- Content (`.content`, `.content__title`, `.content__meta`)
- Organizations (`.org`, `.org-box`, `.organizations-grid`)
- Modals (`.modal`, `.modal-content`)
- Tags and badges (`.tag-chip`, `.badge`)

### Header Styles (`header.css`)
- Main header (`.main-header`)
- Navigation (`.nav-container`, `.nav-links`, `.nav-link`)
- Logo (`.logo-link`, `.logo-icon`, `.logo-text`)

### Footer Styles (`footer.css`)
- Main footer (`.main-footer`)
- Footer content grid (`.footer-content`, `.footer-section`)
- Footer links (`.footer-links`, `.footer-heading`)
- Social media (`.footer-social`, `.social-link`)
- Responsive footer layout

## Responsive Design

All components include responsive design with the following breakpoints:
- **Desktop**: 1200px and above
- **Large Tablet**: 992px - 1199px
- **Tablet**: 768px - 991px
- **Mobile**: 576px - 767px
- **Small Mobile**: Below 576px

## Color Scheme

The application uses a consistent color palette:
- **Primary Blue**: #002D72 (dark blue)
- **Secondary Blue**: #0046B3 (medium blue)
- **Accent Blue**: #60a5fa (light blue)
- **Text Colors**: #111827 (dark), #374151 (medium), #6b7280 (light)
- **Background**: #ffffff (white), #f8fafc (light gray)
- **Borders**: #e2e8f0, #e5e7eb, #d1d5db

## Best Practices

1. **Component Isolation**: Each component has its own CSS file for easy maintenance
2. **BEM Methodology**: Uses BEM-like naming conventions for CSS classes
3. **CSS Variables**: Consider using CSS custom properties for consistent values
4. **Mobile First**: Responsive design starts with mobile and scales up
5. **Performance**: CSS imports are used for development; consider bundling for production

## Maintenance

When adding new styles:
1. Determine if it's a new component or belongs to an existing one
2. Add styles to the appropriate CSS file
3. Update this README if adding new files or major changes
4. Test responsive behavior across all breakpoints
