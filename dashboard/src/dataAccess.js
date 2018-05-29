
export function startGame(){
    fetch("http://localhost:8080/admin/start", {
        credentials: 'include'
    })
    .then( response => {
        if (!response.ok) { throw response }
        return response;  //we only get here if there is no error
    })
    .then( () => {
        this.setState({gameStarted: true});
    })
    .then( () => setTimeout(this.getSchedule, 100))
    .then( () => this.getGameInfo())
    .catch( err => {
        err.text().then( errorMessage => {
            alert(errorMessage);
        })
    });
}

export function getSchedule() {
    fetch("http://localhost:8080/scheduleStrings")
        .then(res => res.json()
            .then(result => {
                this.setState({
                    schedule: result
                })
            })
        ).then(() => setTimeout(this.getSchedule, 30000));
}

export function getPlayers() {
    fetch("http://localhost:8080/players")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        players: result
                    })
                }
            )).then(() => setTimeout(this.getPlayers, 5000));
}

export function getOffers() {
    fetch("http://localhost:8080/offers")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        offers: result
                    })
                }
            )).then(() => setTimeout(this.getOffers, 5000));
}

export function getBids() {
    fetch("http://localhost:8080/bids")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        bids: result
                    })
                }
            )).then(() => setTimeout(this.getBids, 5000));
}

export function getTrades() {
    fetch("http://localhost:8080/trades")
        .then(res => res.json()
            .then(result => {
                    this.setState({
                        trades: result
                    })
                }
            )).then(() => setTimeout(this.getTrades, 5000));
}

export function updateCurrentTime() {
    fetch("http://localhost:8080/time").then(res => res.text()).then((result) => {
        this.setState({currentTime: result})
    }).then(() => setTimeout(this.updateCurrentTime, 1000));
}