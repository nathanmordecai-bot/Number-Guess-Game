<!DOCTYPE html>
<html>
<head>
    <title>Number Guessing Game</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body>

<div id="confetti"></div>

<div class="container <%= request.getAttribute("shake") != null ? "shake" : "" %>">
    <h1>Number Guessing Game</h1>
    <p>Try to guess a number between 1 and 100.</p>

    <% String msg = (String) request.getAttribute("message"); %>
    <% if (msg != null) { %>
        <p class="message"><%= msg %></p>
    <% } %>

    <form action="guess" method="post">
        <input type="text" name="guess" placeholder="Enter a number">
        <br>
        <button type="submit">Submit</button>
    </form>
</div>

<script>
    <% if ("win".equals(request.getAttribute("result"))) { %>
        // Confetti animation
        for (let i = 0; i < 150; i++) {
            let confetti = document.createElement("div");
            confetti.style.position = "absolute";
            confetti.style.width = "8px";
            confetti.style.height = "8px";
            confetti.style.background = "#" + Math.floor(Math.random()*16777215).toString(16);
            confetti.style.top = Math.random() * window.innerHeight + "px";
            confetti.style.left = Math.random() * window.innerWidth + "px";
            confetti.style.opacity = 0.8;
            confetti.style.borderRadius = "50%";
            confetti.style.animation = "fall 2s linear infinite";
            document.getElementById("confetti").appendChild(confetti);
        }
    <% } %>
</script>

</body>
</html>