const eggElement = document.getElementById("egg");
const storyBox = document.getElementById("story-box");
const levelInfoElement = document.getElementById("level-info");
const leaderboardList = document.getElementById("leaderboard");
const saveScoreBtn = document.getElementById("save-score");
const playerNameInput = document.getElementById("player-name");

const clickSound = document.getElementById("click-sound");
const breakSound = document.getElementById("break-sound");
const levelupSound = document.getElementById("levelup-sound");

let level = 1;
let clicks = 0;
let clicksToBreak = 100;
let brokenEggs = new Set();
let eggTypes = [
  { name: "basic", skin: "egg-basic", breakLimit: 100, story: "You started your journey with the basic egg." },
  { name: "gold", skin: "egg-gold", breakLimit: 120, story: "Gold egg unlocked! Shine bright!" },
  { name: "crystal", skin: "egg-crystal", breakLimit: 140, story: "Crystal egg, pure and strong!" },
  { name: "diamond", skin: "egg-diamond", breakLimit: 150, story: "Diamond egg! Legend says it never repeats." }
];

let currentEggIndex = 0;

function updateStory(text) {
  storyBox.innerText = text;
}

function changeEgg() {
  if (currentEggIndex + 1 < eggTypes.length) {
    currentEggIndex++;
    clicks = 0;
    clicksToBreak = eggTypes[currentEggIndex].breakLimit;
    eggElement.className = "egg " + eggTypes[currentEggIndex].skin;
    updateStory(eggTypes[currentEggIndex].story);
    updateLevel();
    playLevelUpSound();
  } else {
    updateStory("You have conquered all legendary eggs! 🏆");
  }
}

function crackEgg() {
  eggElement.classList.add("egg-crack");
  breakSound.play();
  setTimeout(() => {
    eggElement.classList.remove("egg-crack");
  }, 500);
}

function updateLevel() {
  levelInfoElement.innerText = `Level: ${level}`;
}

function playClickSound() {
  clickSound.currentTime = 0;
  clickSound.play();
}

function playLevelUpSound() {
  levelupSound.currentTime = 0;
  levelupSound.play();
}

function saveLeaderboard(name, lvl) {
  if (!name) return;
  let leaderboard = JSON.parse(localStorage.getItem("eggLeaderboard") || "[]");
  leaderboard.push({ name: name, level: lvl });
  leaderboard.sort((a, b) => b.level - a.level);
  leaderboard = leaderboard.slice(0, 5);
  localStorage.setItem("eggLeaderboard", JSON.stringify(leaderboard));
  displayLeaderboard();
}

function displayLeaderboard() {
  let leaderboard = JSON.parse(localStorage.getItem("eggLeaderboard") || "[]");
  leaderboardList.innerHTML = "";
  leaderboard.forEach(entry => {
    let li = document.createElement("li");
    li.textContent = `${entry.name} - Level ${entry.level}`;
    leaderboardList.appendChild(li);
  });
}

// Egg click event
eggElement.addEventListener("click", () => {
  playClickSound();
  clicks++;

  if (clicks >= clicksToBreak) {
    crackEgg();

    if (eggTypes[currentEggIndex].name === "diamond") {
      brokenEggs.add("diamond");
    }

    level++;
    updateLevel();

    // If diamond already broken, no repeat
    if (eggTypes[currentEggIndex].name === "diamond" && brokenEggs.has("diamond")) {
      updateStory("Diamond egg was already broken! You are a true legend!");
      // No more eggs to unlock
      eggElement.style.pointerEvents = "none"; // disable further clicks
      return;
    }

    changeEgg();
  } else {
    updateStory(`Keep clicking! Progressing towards breaking the ${eggTypes[currentEggIndex].name} egg.`);
  }
});

// Save score button event
saveScoreBtn.addEventListener("click", () => {
  const name = playerNameInput.value.trim();
  if (name.length === 0) {
    alert("Please enter your name to save your score.");
    return;
  }
  saveLeaderboard(name, level);
  alert("Score saved! Check leaderboard.");
  playerNameInput.value = "";
});

// Initialize game UI
updateStory(eggTypes[0].story);
updateLevel();
displayLeaderboard();
