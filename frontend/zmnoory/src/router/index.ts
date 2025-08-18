import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import TermsAgreementView from "../views/TermsAgreementView.vue";
import SignupView from "../views/SignupView.vue";
import MainView from "../views/MainView.vue";
import OnboardingView from "../views/OnboardingView.vue";
import VideoRoomView from "../views/catchface/VideoRoomView.vue";
import ControlPanelView from "../views/ControlPanelView.vue";

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "onboarding",
      component: OnboardingView,
    },
    {
      path: "/main",
      name: "main",
      component: MainView,
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/terms",
      name: "terms",
      component: TermsAgreementView,
    },
    {
      path: "/signup",
      name: "signup",
      component: SignupView,
    },
    {
      path: "/games",
      name: "games",
      component: () => import("@/views/GameListView.vue"),
      meta: { requiresAuth: true },
    },
    // 👇 [추가] 상세 페이지를 위한 동적 라우트
    {
      path: "/games/:id",
      name: "GameDetail",
      // GameDetailView.vue 파일을 동적으로 불러옵니다.
      component: () => import("@/views/GameDetailView.vue"),
      meta: { requiresAuth: true },
      // URL의 :id 값을 컴포넌트의 props로 전달합니다.
      props: true,
    },
    {
      path: "/gametest",
      name: "GameTestView.vue",
      component: () => import("@/views/TestView/GameTestView.vue"),
    },
    {
      path: "/videos",
      name: "Videos",
      component: () => import("@/views/VideoListView.vue"),
    },
    {
      path: "/store",
      name: "Store",
      component: () => import("@/views/StoreView.vue"),
    },
    {
      path: "/data-request",
      name: "DataRequest",
      component: () => import("@/views/DataRequestView.vue"),
    },
    {
      path: "/room/:roomId",
      name: "room",
      component: VideoRoomView,
      props: true,
    },
    {
      path: "/mypage",
      name: "mypage",
      component: () => import("@/views/MyPageView.vue"),
    },
    {
      path: "/control-panel",
      name: "control-panel",
      component: ControlPanelView,
      meta: { requiresAuth: true },
    },
    {
      path: "/videos/:id",
      name: "VideoDetail",
      // GameDetailView.vue 파일을 동적으로 불러옵니다.
      component: () => import("@/views/VideoDetailView.vue"),
      meta: { requiresAuth: true },
      // URL의 :id 값을 컴포넌트의 props로 전달합니다.
      props: true,
    },
    {
      path: "/webrtc-test",
      name: "WebRTCTest",
      component: () => import("@/views/webrtc-test/WebRTCTestView.vue"),
    },
    {
      path: "/guess-expression",
      name: "GuessExpression",
      component: () =>
        import(
          "@/modules/games/guess-expression/GuessExpressionGameComponent.vue"
        ),
    },
  ],
});

router.beforeEach((to) => {
  const token = sessionStorage.getItem("accessToken");
  if (to.meta.requiresAuth && !token) {
    // 게임 관련 페이지일 때 알람 표시
    if (to.name === "games" || to.name === "GameDetail") {
      alert("게임을 하려면 로그인이 필요합니다.");
    }
    return { name: "login", query: { redirect: to.fullPath } };
  }
});

export default router;
