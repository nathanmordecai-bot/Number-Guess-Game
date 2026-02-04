<!DOCTYPE html>
<html>
<head>
    <title>Number Guessing Game</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body>
    <div class="container">
        <h1>Welcome to the Number Guessing Game!</h1>
        <p>Try to guess a number between 1 and 100.</p>

        <form action="guess" method="post">
            <label>Enter your guess:</label><br>
            <input type="text" name="guess" placeholder="Enter a number">
            <br><br>
            <button type="submit">Submit</button>
        </form>
    </div>
</body>
</html>