import { useAuthStore } from '../../store/authStore';
import { useOnboardingStore } from '../../store/onboardingStore';
import { useUiStore } from '../../store/uiStore';

describe('Zustand stores', () => {
  beforeEach(() => {
    useAuthStore.setState({ session: null, user: null });
    useOnboardingStore.setState({ completed: [] });
    useUiStore.setState({ sidebarOpen: false, bottomSheet: null });
  });

  describe('authStore', () => {
    it('sets and clears session', () => {
      const mockSession = { accessToken: 'token', user: { id: '1', email: 'test@test.com', role: 'user' } };
      useAuthStore.getState().setSession(mockSession);
      expect(useAuthStore.getState().session).toEqual(mockSession);
      expect(useAuthStore.getState().user).toEqual(mockSession.user);

      useAuthStore.getState().clear();
      expect(useAuthStore.getState().session).toBeNull();
      expect(useAuthStore.getState().user).toBeNull();
    });

    it('updates user partially', () => {
      useAuthStore.getState().setSession({ accessToken: 't', user: { id: '1', email: 'a@b.com', role: 'user' } });
      useAuthStore.getState().setUser({ displayName: 'New Name' });
      expect(useAuthStore.getState().user?.displayName).toBe('New Name');
    });
  });

  describe('onboardingStore', () => {
    it('tracks completed steps', () => {
      useOnboardingStore.getState().markStep('profile');
      useOnboardingStore.getState().markStep('location');
      expect(useOnboardingStore.getState().completed).toEqual(['profile', 'location']);
      expect(useOnboardingStore.getState().isComplete()).toBe(false);
    });

    it('marks complete after all steps', () => {
      const { ONBOARDING_STEPS } = require('@manabandhu/contracts');
      ONBOARDING_STEPS.forEach((s: string) => useOnboardingStore.getState().markStep(s as any));
      expect(useOnboardingStore.getState().isComplete()).toBe(true);
    });

    it('resets', () => {
      useOnboardingStore.getState().markStep('profile');
      useOnboardingStore.getState().reset();
      expect(useOnboardingStore.getState().completed).toEqual([]);
    });
  });

  describe('uiStore', () => {
    it('toggles sidebar', () => {
      useUiStore.getState().toggleSidebar();
      expect(useUiStore.getState().sidebarOpen).toBe(true);
      useUiStore.getState().toggleSidebar();
      expect(useUiStore.getState().sidebarOpen).toBe(false);
    });

    it('manages bottom sheet', () => {
      useUiStore.getState().openBottomSheet('filter');
      expect(useUiStore.getState().bottomSheet).toBe('filter');
      useUiStore.getState().closeBottomSheet();
      expect(useUiStore.getState().bottomSheet).toBeNull();
    });
  });
});