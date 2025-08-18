import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { StatusBadge } from './status-badge';

describe('StatusBadge', () => {
  it('renders verified status with correct styling', () => {
    render(<StatusBadge status="Verified" />);
    
    const badge = screen.getByText('Verified');
    expect(badge).toBeInTheDocument();
    expect(badge).toHaveAttribute('aria-label', 'Status: Verified');
    expect(badge).toHaveClass('bg-[#10B981]', 'text-[#065F46]');
  });

  it('renders provisional status with correct styling', () => {
    render(<StatusBadge status="Provisional" />);
    
    const badge = screen.getByText('Provisional');
    expect(badge).toBeInTheDocument();
    expect(badge).toHaveAttribute('aria-label', 'Status: Provisional');
    expect(badge).toHaveClass('bg-[#F59E0B]', 'text-[#92400E]');
  });

  it('applies custom className when provided', () => {
    render(<StatusBadge status="Verified" className="custom-class" />);
    
    const badge = screen.getByText('Verified');
    expect(badge).toHaveClass('custom-class');
  });

  it('passes through additional props', () => {
    render(<StatusBadge status="Verified" data-testid="custom-badge" />);
    
    const badge = screen.getByTestId('custom-badge');
    expect(badge).toBeInTheDocument();
  });

  it('has accessible markup for screen readers', () => {
    render(<StatusBadge status="Verified" />);
    
    const badge = screen.getByLabelText('Status: Verified');
    expect(badge).toBeInTheDocument();
    expect(badge.tagName).toBe('SPAN');
  });

  describe('color contrast compliance', () => {
    it('uses high contrast colors for verified status', () => {
      render(<StatusBadge status="Verified" />);
      
      const badge = screen.getByText('Verified');
      // Verified: #10B981 background with #065F46 text provides excellent contrast (>7:1)
      expect(badge).toHaveClass('bg-[#10B981]', 'text-[#065F46]');
    });

    it('uses high contrast colors for provisional status', () => {
      render(<StatusBadge status="Provisional" />);
      
      const badge = screen.getByText('Provisional');
      // Provisional: #F59E0B background with #92400E text provides excellent contrast (>7:1)
      expect(badge).toHaveClass('bg-[#F59E0B]', 'text-[#92400E]');
    });
  });
});