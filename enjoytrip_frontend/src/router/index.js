import { createRouter, createWebHistory } from "vue-router/auto";

import Home from "@/pages/Home.vue";
import HotPlace from "@/pages/HotPlace.vue";
import Board from "@/pages/Board.vue";
import Attraction from "@/pages/Attraction.vue";
import AttractionBoard from "@/pages/AttractionBoard.vue";
import Login from "@/pages/Login.vue";
import SignUp from "@/pages/SignUp.vue";

import BoardDeatil from "@/components/board/BoardDeatil.vue";
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", component: Home, name: "home" },
    { path: "/hotplace", component: HotPlace, name: "hotplace" },
    {
      path: "/board",
      component: Board,
      name: "board",
    },
    {
      path: "/board/:id",
      component: BoardDeatil,
    },
    { path: "/attraction", component: Attraction, name: "attraction" },
    { path: "/attraction-board", component: AttractionBoard },
    { path: "/login", component: Login },
    { path: "/signup", component: SignUp },
  ],
  linkExactActiveClass: "active-link",
});

export default router;
