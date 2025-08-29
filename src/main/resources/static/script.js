const form = document.getElementById('search-form');
const input = document.getElementById('q');
const btn = document.getElementById('btn-search');
const results = document.getElementById('results');
const statusEl = document.getElementById('status');
const alerts = document.getElementById('alerts');
const chips = document.getElementById('active-filters');

const yearFromEl = document.getElementById('yearFrom');
const yearToEl = document.getElementById('yearTo');
const minImdbEl = document.getElementById('minImdbRating');
const excludeGenres = document.getElementById('excludeGenres');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const q = input.value.trim();
    if (!q) return;

    setBusy(true);
    clearUI();

    try {
        const genres = [...document.querySelectorAll('.genre:checked')].map(x => x.value);

        const req = { query: q };
        if (yearFromEl.value) req.yearFrom = Number(yearFromEl.value);
        if (yearToEl.value) req.yearTo = Number(yearToEl.value);
        if (genres.length) req.genres = genres;
        if (minImdbEl.value) req.minImdbRating = Number(minImdbEl.value);
        if (excludeGenres.checked) req.excludeGenres = true;

        renderChips(req);

        const resp = await fetch('/movies/search', {
            method: 'POST',
            headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' },
            body: JSON.stringify(req)
        });
        if (!resp.ok) throw new Error('HTTP ' + resp.status);

        const data = await resp.json();
        if (!Array.isArray(data) || data.length === 0) {
            showAlert('No results found.', 'warning');
            return;
        }

        statusEl.textContent = `Found ${data.length} result${data.length > 1 ? 's' : ''}.`;
        renderResults(data);

    } catch (err) {
        showAlert('Error fetching movies: ' + String(err?.message || err), 'danger');
    } finally {
        setBusy(false);
    }


});

function renderChips(req) {
    const items = [];
    if (req.yearFrom != null || req.yearTo != null) items.push(['Year', `${req.yearFrom ?? ''}–${req.yearTo ?? ''}`]);
    if (req.genres && req.genres.length) items.push(['Genres', req.genres.join(', ')]);
    if (req.minImdbRating != null) items.push(['IMDb ≥', req.minImdbRating]);
    chips.innerHTML = items.map(([k, v]) =>
        `<span class="badge rounded-pill text-bg-secondary filter-chip">${k}: ${v}</span>`).join('');
}

function renderResults(items) {
    for (const m of items) {
        const title   = pick(m, ['title','name']) || '(untitled)';
        const year    = pick(m, ['year','releasedYear','releaseYear']);
        const plot    = pick(m, ['fullplot','fullPlot','plot']) || '';
        const rating  = m?.imdb?.rating;
        const genres  = Array.isArray(m?.genres) ? m.genres : [];
        const poster  = m.poster || 'https://i.ibb.co/hRGmNYDn/No-image-Available.png';

        const col = document.createElement('div');
        col.className = 'col-6 col-md-4 col-lg-3';

        const card = document.createElement('div');
        card.className = 'card h-100 movie-card shadow-sm';

        const img = document.createElement('img');
        img.src = poster;
        img.alt = title;
        img.className = 'movie-poster card-img-top';
        card.appendChild(img);

        const body = document.createElement('div');
        body.className = 'card-body d-flex flex-column';

        const h5 = document.createElement('h5');
        h5.className = 'movie-title';
        h5.textContent = title;
        body.appendChild(h5);

        if (year || rating != null) {
            const meta = document.createElement('div');
            meta.className = 'movie-meta';
            const parts = [];
            if (year) parts.push(String(year));
            if (rating != null) {
                parts.push(`★ ${Number(rating).toFixed(1)}`);
            }
            meta.textContent = parts.join(' • ');
            body.appendChild(meta);
        }

        if (genres.length) {
            const wrap = document.createElement('div');
            const maxToShow = 3;
            genres.slice(0, maxToShow).forEach(g => {
                const chip = document.createElement('span');
                chip.className = 'genre-chip';
                chip.textContent = g;
                wrap.appendChild(chip);
            });
            if (genres.length > maxToShow) {
                const more = document.createElement('span');
                more.className = 'genre-chip genre-more';
                more.textContent = `+${genres.length - maxToShow}`;
                wrap.appendChild(more);
            }
            body.appendChild(wrap);
        }

        if (plot) {
            const p = document.createElement('p');
            p.className = 'movie-plot';
            p.textContent = plot;
            body.appendChild(p);
        }

        card.appendChild(body);
        col.appendChild(card);
        results.appendChild(col);
    }
}

function formatVotes(v) {
    const n = Number(v);
    if (!Number.isFinite(n)) return v;
    if (n >= 1_000_000) return (n / 1_000_000).toFixed(1).replace(/\.0$/, '') + 'M';
    if (n >= 1_000)     return (n / 1_000).toFixed(1).replace(/\.0$/, '') + 'K';
    return String(n);
}


function setBusy(isBusy) {
    btn.disabled = isBusy;
    input.disabled = isBusy;
    statusEl.innerHTML = isBusy
        ? '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>Searching…'
        : '';
}

function clearUI() {
    alerts.innerHTML = '';
    results.innerHTML = '';
    statusEl.textContent = '';
    chips.innerHTML = '';
}

function showAlert(msg, type = 'info') {
    const div = document.createElement('div');
    div.className = `alert alert-${type}`;
    div.textContent = msg;
    alerts.appendChild(div);
}

function pick(obj, keys) {
    for (const k of keys) {
        if (obj && obj[k] != null && String(obj[k]).trim() !== '') return obj[k];
    }
    return undefined;
}
