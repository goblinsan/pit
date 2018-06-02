
export function getSchedule() {
    fetch("/scheduleStrings")
        .then(res => res.json()
            .then(result => {
                this.setState({
                    schedule: result
                })
            })
        ).then(() => setTimeout(this.getSchedule, 30000));
}

export function getPlayers() {
    fetch("/players")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        players: result
                    })
                }
            )).then(() => setTimeout(this.getPlayers, 5000));
}

export function getTrades() {
    fetch("/trades")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        trades: result
                    })
                }
            )).then(() => setTimeout(this.getTrades, 5000));
}

export function getOffers() {
    fetch("/offers")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        offers: result
                    })
                }
            )).then(() => setTimeout(this.getOffers, 5000));
}

export function getBids() {
    fetch("/bids")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        bids: result
                    })
                }
            )).then(() => setTimeout(this.getBids, 5000));
}

export function updateCurrentTime() {
    fetch("/time").then(res => res.text()).then((result) => {
        this.setState({currentTime: result})
    }).then(() => setTimeout(this.updateCurrentTime, 1000));
}

export const setCookie = (name, value, days, path = '/') => {
    let expires = '';
    if (days) {
        let date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = `; expires=${date.toUTCString()};`;
    }
    document.cookie = `${name}=${value}${expires}; path=${path}`;
};

export const getCookie = (cookieName) => {
    if (document.cookie.length > 0) {
        let cookieStart = document.cookie.indexOf(cookieName + '=');
        if (cookieStart !== -1) {
            cookieStart = cookieStart + cookieName.length + 1;
            let cookieEnd = document.cookie.indexOf(';', cookieStart);
            if (cookieEnd === -1) {
                cookieEnd = document.cookie.length;
            }
            return window.unescape(document.cookie.substring(cookieStart, cookieEnd));
        }
    }
    return '';
};