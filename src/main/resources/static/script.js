document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");
    const loginForm = document.getElementById("loginForm");

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);
            const roles = Array.from(formData.getAll("roles"));

            const payload = {
                username: formData.get("username"),
                email: formData.get("email"),
                password: formData.get("password"),
                roles: roles,
            };

            try {
                const response = await fetch("/api/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });

                if (response.ok) {
                    alert("Registration successful! Redirecting to login page...");
                    window.location.href = "login.html";
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (error) {
                console.error("Error during registration:", error);
                alert("An error occurred. Please try again.");
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(loginForm);

            const payload = {
                username: formData.get("username"),
                password: formData.get("password"),
            };

            try {
                const response = await fetch("/api/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });

                if (response.ok) {
                    alert("Login successful!");
                    // Redirect to dashboard or another page
                    window.location.href = "dashboard.html";
                } else {
                    const error = await response.text();
                    alert("Error: " + error);
                }
            } catch (error) {
                console.error("Error during login:", error);
                alert("An error occurred. Please try again.");
            }
        });
    }
});
