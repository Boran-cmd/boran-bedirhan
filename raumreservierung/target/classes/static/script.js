document.addEventListener("DOMContentLoaded", () => {
    // ===== CREATE =====
    const createForm = document.getElementById("createForm");
    if (createForm) {
        createForm.addEventListener("submit", function (e) {
            e.preventDefault();

            const data = {
                date: document.getElementById("date").value,
                startTime: document.getElementById("startTime").value,
                endTime: document.getElementById("endTime").value,
                roomName: document.getElementById("roomName").value,
                remark: document.getElementById("remark").value,
                participants: document.getElementById("participants").value,
            };

            fetch("/api/reservation", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
            })
                .then((res) => {
                    if (!res.ok) throw res;
                    return res.json();
                })
                .then((response) => {
                    document.getElementById("result").innerHTML =
                        `<p class="success-message">✅ Reservierung erfolgreich!<br><strong>Public Key:</strong> ${response.publicKey}<br><strong>Private Key:</strong> ${response.privateKey}</p>`;
                    createForm.reset();
                })
                .catch(async (err) => {
                    const msg = await err.text();
                    document.getElementById("result").innerHTML =
                        `<p class="error-message">❌ ${msg}</p>`;
                });
        });
    }

    // ===== VIEW =====
    const viewForm = document.getElementById("viewForm");
    if (viewForm) {
        viewForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const key = document.getElementById("publicKey").value;

            fetch(`/api/reservation/public/${key}`)
                .then((res) => {
                    if (!res.ok) throw new Error("Reservierung nicht gefunden.");
                    return res.json();
                })
                .then((data) => {
                    document.getElementById("reservationInfo").style.display = "block";
                    document.getElementById("resDate").textContent = data.date;
                    document.getElementById("resTime").textContent = `${data.startTime} – ${data.endTime}`;
                    document.getElementById("resRoom").textContent = data.room.name || data.room.roomNumber;
                    document.getElementById("resRemark").textContent = data.remark;
                    document.getElementById("resParticipants").textContent = data.participants;
                })
                .catch((err) => {
                    document.getElementById("viewMessage").textContent = err.message;
                });
        });
    }

    // ===== EDIT / LOAD =====
    const loadForm = document.getElementById("loadForm");
    const editForm = document.getElementById("editForm");
    if (loadForm && editForm) {
        window.loadReservation = function () {
            const key = document.getElementById("privateKey").value;

            fetch(`/api/reservation/private/${key}`)
                .then((res) => {
                    if (!res.ok) throw new Error("Reservierung nicht gefunden.");
                    return res.json();
                })
                .then((data) => {
                    editForm.style.display = "block";
                    editForm.dataset.key = key;
                    document.getElementById("date").value = data.date;
                    document.getElementById("startTime").value = data.startTime;
                    document.getElementById("endTime").value = data.endTime;
                    document.getElementById("remark").value = data.remark;
                    if (Array.isArray(data.participants)) {
                        document.getElementById("participants").value = data.participants.map(p => p.name).join(", ");
                    } else {
                        document.getElementById("participants").value = data.participants;
                    }

                })
                .catch((err) => {
                    document.getElementById("editMessage").textContent = err.message;
                });
        };

        editForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const key = editForm.dataset.key;

            const data = {
                date: document.getElementById("date").value,
                startTime: document.getElementById("startTime").value,
                endTime: document.getElementById("endTime").value,
                remark: document.getElementById("remark").value,
                participants: document.getElementById("participants").value,
            };

            fetch(`/api/reservation/private/${key}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
            })
                .then((res) => {
                    if (!res.ok) throw new Error("Fehler beim Speichern.");
                    document.getElementById("editMessage").textContent = "✅ Reservierung aktualisiert.";
                })
                .catch((err) => {
                    document.getElementById("editMessage").textContent = err.message;
                });
        });

        window.deleteReservation = function () {
            const key = editForm.dataset.key;

            fetch(`/api/reservation/private/${key}`, {
                method: "DELETE",
            })
                .then((res) => {
                    if (!res.ok) throw new Error("Fehler beim Löschen.");
                    document.getElementById("editMessage").textContent = "🗑️ Reservierung gelöscht.";
                    editForm.reset();
                    editForm.style.display = "none";
                })
                .catch((err) => {
                    document.getElementById("editMessage").textContent = err.message;
                });
        };
    }

    // ===== ADMIN =====
    const filterForm = document.getElementById("filterForm");
    if (filterForm) {
        filterForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const date = document.getElementById("filterDate").value;

            fetch(`/api/reservation/admin/by-date?date=${date}`)
                .then((res) => res.json())
                .then(displayAdminTable)
                .catch(() => {
                    document.getElementById("adminMessage").textContent = "❌ Fehler beim Filtern.";
                });
        });
    }

    window.loadAll = function () {
        fetch("/api/reservation/admin/all")
            .then((res) => res.json())
            .then(displayAdminTable)
            .catch(() => {
                document.getElementById("adminMessage").textContent = "❌ Fehler beim Laden.";
            });
    };

    function displayAdminTable(data) {
        document.getElementById("adminTableSection").style.display = "block";
        const tbody = document.getElementById("reservationRows");
        tbody.innerHTML = "";

        data.forEach((res) => {
            const row = document.createElement("tr");
            row.innerHTML = `
        <td>${res.date}</td>
        <td>${res.startTime} – ${res.endTime}</td>
        <td>${res.room.name || res.room.roomNumber}</td>
        <td>${res.participants}</td>
        <td>${res.remark}</td>
      `;
            tbody.appendChild(row);
        });
    }
});
