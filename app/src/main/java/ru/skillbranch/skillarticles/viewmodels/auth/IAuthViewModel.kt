package ru.skillbranch.skillarticles.viewmodels.auth

interface IAuthViewModel {
    fun handleLogin(login: String, pass: String, dest: Int?)
}