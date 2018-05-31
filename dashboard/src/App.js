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

        this.startGame = DataAccess.startGame.bind(this);
        this.getGameInfo = this.getGameInfo.bind(this);
        this.onLogin = this.onLogin.bind(this);
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

    onLogin(token){
        this.setState({isAuthenticated: true});
        DataAccess.setCookie("PitGame", token);
    }

    componentDidMount() {
        if(DataAccess.getCookie("PitGame")){
            this.setState({isAuthenticated: true});
        }
        this.setState({isLoaded: true});
    }

    render() {
        return (
            <Router history={history}>
                <div>
                    <div className="App">
                        <Navbar inverse>
                            <Navbar.Header>
                                <Navbar.Brand>
                                    Pit Trading Server
                                </Navbar.Brand>
                            </Navbar.Header>
                        </Navbar>
                        <GameDashboard data={this.state} onLogin={this.onLogin} startGame={this.startGame} />
                    </div>
                </div>
            </Router>
        );
    }
}

export default App;
