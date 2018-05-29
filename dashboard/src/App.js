import React, {Component} from 'react';
import {Button, ControlLabel, FormControl, FormGroup, Grid, Navbar, Panel, Row} from 'react-bootstrap';
import './App.css';
import Col from "react-bootstrap/es/Col";
import BidTable from "./Bids";
import ScheduleTable from "./ScheduleTable";
import PlayerTable from "./Players";
import OfferTable from "./Offers";
import TradeTable from "./Trades";
import * as DataAccess from "./dataAccess.js";
import {BrowserRouter as Router} from "react-router-dom";


class StartGameButton extends React.Component {
    render() {
        if (this.props.gameStarted) {
            return null;
        } else {
            return (
                <Button bsSize="large" bsStyle="success" onClick={this.props.startGame}>Start Game</Button>
            );
        }
    }
}

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            showModal: false,
            loading: false,
            currentTime: null,
            gameStarted: false,
            schedule: [],
            players: [],
            offers: [],
            bids: [],
            trades: [],
            username: "",
            password: ""
        };
        this.getGameInfo = this.getGameInfo.bind(this);
        this.startGame = DataAccess.startGame.bind(this);
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

    componentDidMount() {
        this.setState({isLoaded: true});
    }

    validateForm() {
        return this.state.username.length > 0 && this.state.password.length > 0;
    }

    onChange = (e) => {
        this.setState({[e.target.id]: e.target.value});
    };

    handleSubmit = event => {
        event.preventDefault();
        let formData = new FormData();
        formData.append("username", this.state.username);
        formData.append("password", this.state.password);
        fetch('http://localhost:8080/login', {
            method: 'POST',
            body: formData,
            credentials: 'include'
        }).then(function(res) {
            if (res.ok) {
                console.log("Logged in");
            } else if (res.status === 401) {
                alert("Oops! You are not authorized.");
            }
        }, function(e) {
            alert("Error submitting form!");
        });
    };

    render() {
        return (
            <Router>
                <div>
                    <div className="App">
                        <Navbar inverse>
                            <Navbar.Header>
                                <Navbar.Brand>
                                    Pit Trading Server
                                </Navbar.Brand>
                                <StartGameButton
                                    gameStarted={this.state.gameStarted}
                                    startGame={this.startGame}
                                />
                            </Navbar.Header>
                        </Navbar>

                        <Grid>
                            <Panel>
                                <Panel.Body>
                                    <Row className="show-grid">
                                        <Col md={4} >
                                            <Panel>
                                                <Panel.Heading>Schedule</Panel.Heading>
                                                <Panel.Body>
                                                    <ScheduleTable
                                                        currentTime={this.state.currentTime}
                                                        schedule={this.state.schedule}
                                                        gameStarted={this.state.gameStarted}
                                                    />
                                                </Panel.Body>
                                            </Panel>
                                        </Col>
                                        <Col md={4} >
                                            <Panel>
                                                <Panel.Heading>Players</Panel.Heading>
                                                <Panel.Body>
                                                    <PlayerTable players={this.state.players}/>
                                                </Panel.Body>
                                            </Panel>
                                        </Col>
                                        <Col md={4} >
                                            <Panel>
                                                <Panel.Heading>Offers</Panel.Heading>
                                                <Panel.Body>
                                                    <OfferTable offers={this.state.offers}/>
                                                </Panel.Body>
                                            </Panel>
                                        </Col>
                                    </Row>
                                    <Row className="show-grid">
                                        <Col md={6} >
                                            <Panel>
                                                <Panel.Heading>Bids</Panel.Heading>
                                                <Panel.Body>
                                                    <BidTable bids={this.state.bids}/>
                                                </Panel.Body>
                                            </Panel>
                                        </Col>
                                        <Col md={6} >
                                            <Panel>
                                                <Panel.Heading>Trades</Panel.Heading>
                                                <Panel.Body>
                                                    <TradeTable trades={this.state.trades}/>
                                                </Panel.Body>
                                            </Panel>
                                        </Col>
                                    </Row>
                                </Panel.Body>
                            </Panel>
                            <Panel>
                                <Panel.Body>
                                    <div id={"formForLogin"}>
                                        <form onSubmit={this.handleSubmit}>
                                            <FormGroup controlId="username" bsSize="large">
                                                <ControlLabel>User</ControlLabel>
                                                <FormControl
                                                    autoFocus
                                                    type="text"
                                                    value={this.state.username}
                                                    onChange={this.onChange}
                                                />
                                            </FormGroup>
                                            <FormGroup controlId="password" bsSize="large">
                                                <ControlLabel>Password</ControlLabel>
                                                <FormControl
                                                    value={this.state.password}
                                                    onChange={this.onChange}
                                                    type="password"
                                                />
                                            </FormGroup>
                                            <Button
                                                block
                                                bsSize="large"
                                                disabled={!this.validateForm()}
                                                type="submit"
                                            >
                                                Login
                                            </Button>
                                        </form>
                                    </div>
                                </Panel.Body>
                            </Panel>
                        </Grid>
                    </div>
                </div>

            </Router>
        );
    }
}

export default App;
