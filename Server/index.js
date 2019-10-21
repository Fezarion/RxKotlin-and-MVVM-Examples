const express = require("express");
const bodyParser = require("body-parser");

const app = express();
let sport = "soccer";

let id = 12;

let spendings = [
    { id: 12, title: "Ice Cream", description: "Hagen Dazs", value: 10 },
    { id: 11, title: "Plane", description: "Queenstown", value: 80 },
    { id: 10, title: "Dinner", description: "Paella", value: 40 },
    { id: 9, title: "Hotel", description: "Ramada Hotel", value: 100 },
    { id: 8, title: "Hobby", description: "Snowboard", value: 1500 },
    { id: 7, title: "Car", description: "Fuel", value: 100 },
    { id: 6, title: "Utility", description: "Gas", value: 40 },
    { id: 5, title: "Utility", description: "Electricity", value: 80 },
    { id: 4, title: "Lunch", description: "Mince and Cheese Pie", value: 4 },
    { id: 3, title: "Lunch", description: "Steak Mignon", value: 40 },
    { id: 2, title: "Grocery", description: "Milk", value: 4 },
    { id: 1, title: "Grocery", description: "Carrots", value: 5 }
];

let previousSpendings = [];

app.use(bodyParser.json());

// JSON validator
app.use((err, req, res, next) => {
    if (err && err.type === "entity.parse.failed") {
        res.status(400).send({ error: "JSON parse error" });
    } else {
        next();
    }
});

app.get("/api/test", (req, res) => {
    res.send({ test: "Hello World", test2: "Hello", test3: "World" });
});

app.get("/api/test2", (req, res) => {
    res.send({ test: "Jello" });
});

app.get("/api/test3", (req, res) => {
    res.send({ test: "Jelly" });
});

app.get("/api/sport", (req, res) => {
    res.send({ sport });
});

app.put("/api/sport", (req, res) => {
    const old = sport;
    sport = req.body.sport;
    if (typeof sport === "string" || sport instanceof String) {
        res.send({ sport, old });
    } else {
        res.status(403).send({ error: "body is not a string." });
    }
});

app.get("/api/spending", (req, res) => {
    res.send(spendings);
});

app.post("/api/spending", spendingHasValue, (req, res) => {
    const spending = req.body;

    id++;
    spending.id = id;

    spendings.unshift(spending);
    res.send(spendings);
});

app.put("/api/spending", spendingHasValue, (req, res) => {
    const spending = req.body;
    const i = spendings.findIndex(value => value.id === spending.id);

    if (i === -1) {
        return res
            .status(404)
            .send({ error: `Can't find spending with id ${spending.id}` });
    }

    spendings[i] = spending;
    res.send(spendings);
});

app.delete("/api/spending/all", (req, res) => {
    previousSpendings = spendings;
    spendings = [];
    res.send(spendings);
});

app.delete("/api/spending/:id", spendingHasValue, (req, res) => {
    const id = parseInt(req.params.id);
    const newSpendings = spendings.filter(value => value.id !== id);

    if (newSpendings.length === spendings.length) {
        return res
            .status(404)
            .send({ error: `Can't find spending with id ${id}` });
    }

    previousSpendings = spendings;
    spendings = newSpendings;
    res.send(spendings);
});


app.get("/api/spending/undo", (req, res) => {
    spendings = previousSpendings;
    res.send(spendings);
});

function spendingHasValue(req, res, next) {
    const spending = req.body;
    if (spending) {
        next();
    } else {
        res.status(403).send({ error: "Spending can't be null" });
    }
}

app.listen(5000);

console.log("Server listening on port", 5000);
