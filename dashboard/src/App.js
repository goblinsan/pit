import React, {Component} from 'react';
import {Navbar} from 'react-bootstrap';
import './App.css';
import * as DataAccess from "./dataAccess.js";
import {Router} from "react-router-dom";
import createBrowserHistory from 'history/createBrowserHistory';
import GameDashboard from "./GameDashboard";

const history = createBrowserHistory();


class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            gameStarted: false,
            isAuthenticated: false,
            userType: null,
            currentTime: null,
            schedule: [],
            players: [],
            offers: [],
            bids: [],
            trades: []
        };

        this.getGameInfo = this.getGameInfo.bind(this);
        this.onLogin = this.onLogin.bind(this);
        this.updateSchedule = this.updateSchedule.bind(this);
        this.updateCurrentTime = DataAccess.updateCurrentTime.bind(this);
        this.getSchedule = DataAccess.getSchedule.bind(this);
        this.getPlayers = DataAccess.getPlayers.bind(this);
        this.getOffers = DataAccess.getOffers.bind(this);
        this.getBids = DataAccess.getBids.bind(this);
        this.getTrades = DataAccess.getTrades.bind(this);

    }

    getGameInfo() {
        this.updateCurrentTime();
        this.getSchedule();
        this.getPlayers();
        this.getOffers();
        this.getBids();
        this.getTrades();
    }

    onLogin(token) {
        this.setState({isAuthenticated: true});
        DataAccess.setCookie("PitGame", token);
    }

    updateSchedule(){
        this.setState({gameStarted:true});
        setTimeout(this.getSchedule, 100);
    }

    componentDidMount() {
        this.setState({isLoaded: true});
        if (DataAccess.getCookie("PitGame")) {
            this.setState({isAuthenticated: true});
        }
        this.getGameInfo();
    }

    render() {
        return (
            <Router history={history}>
                <div>
                    <div className="App">
                        <Navbar inverse fluid>
                            <Navbar.Header>
                                <Navbar.Brand>
                                    Pit Trading Server
                                </Navbar.Brand>
                            </Navbar.Header>
                        </Navbar>
                        <GameDashboard gameData={this.state} onLogin={this.onLogin} updateSchedule={this.updateSchedule} history={history} />
                    </div>
                </div>
            </Router>
        );
    }
}

export default App;
