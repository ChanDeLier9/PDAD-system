import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
    state: () => ({
        doctorId: null, // 初始值为 null
        doctorInfo: {}, // 初始值为空对象
        isAuthenticated: false, // 初始值为未登录
        jwtToken: null, // 初始值为 null
        patients: [], // 存储患者信息列表
    }),
    actions: {
        initializeStore() {
            // 从 localStorage 恢复状态
            this.doctorId = localStorage.getItem("doctorId") || null;
            this.doctorInfo = JSON.parse(localStorage.getItem("doctorInfo") || "{}");
            this.isAuthenticated = localStorage.getItem("isAuthenticated") === "true";
            this.jwtToken = localStorage.getItem("jwtToken") || null;
        },
        setDoctorId(doctorId) {
            this.doctorId = doctorId; // 使用传入的 doctorId
            localStorage.setItem("doctorId", doctorId); // 同步保存到 localStorage
        },
        setDoctorInfo(info) {
            this.doctorInfo = info;
            localStorage.setItem("doctorInfo", JSON.stringify(info));
        },
        setAuthenticationStatus(status) {
            this.isAuthenticated = status;
            localStorage.setItem("isAuthenticated", String(status));
        },
        // 设置 JWT 令牌
        setJwtToken(token) {
            this.jwtToken = token;
            localStorage.setItem("jwtToken", token); // 存储到 localStorage
        },
        logout() {
            this.doctorId = null;
            this.doctorInfo = {};
            this.isAuthenticated = false;
            this.jwtToken = null; // 清除 JWT 令牌
            this.patients = [];

            // 清除 localStorage 中的所有相关数据
            localStorage.removeItem("doctorId");
            localStorage.removeItem("doctorInfo");
            localStorage.removeItem("isAuthenticated");
            localStorage.removeItem("jwtToken");
            },
        setPatients(patients) {
            this.patients = patients;
        },
    },
    getters: {
        // 计算属性：获取患者数量
        patientCount: (state) => state.patients.length,
        doctorName: (state) => state.doctorInfo?.name || "", // 从 doctorInfo 中获取名字
    },
    persist: {
        key: "userStore",
        storage: localStorage,
    },
});
