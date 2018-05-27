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
import {BrowserRouter as Router, Link, Redirect, Route, withRouter} from "react-router-dom";


const fakeAuth = {
    isAuthenticated: false,
    authenticate(cb) {
        this.isAuthenticated = true;
        setTimeout(cb, 100); // fake async
    },
    signout(cb) {
        this.isAuthenticated = false;
        setTimeout(cb, 100);
    }
};

const AuthButton = withRouter(
    ({ history }) =>
        fakeAuth.isAuthenticated ? (
            <p>
                Welcome!{" "}
                <button
                    onClick={() => {
                        fakeAuth.signout(() => history.push("/"));
                    }}
                >
                    Sign out
                </button>
            </p>
        ) : (
            <p>You are not logged in.</p>
        )
);

const PrivateRoute = ({ component: Component, ...rest }) => (
    <Route
        {...rest}
        render={props =>
            fakeAuth.isAuthenticated ? (
                <Component {...props} />
            ) : (
                <Redirect
                    to={{
                        pathname: "/login",
                        state: { from: props.location }
                    }}
                />
            )
        }
    />
);

const Public = () => <h3>Public</h3>;
const Chucky = () => <h3>Logged in homie</h3>;
const Protected = () => <h3>Protected</h3>;

class Login extends React.Component {
    state = {
        redirectToReferrer: false
    };

    login = () => {
        fakeAuth.authenticate(() => {
            this.setState({ redirectToReferrer: true });
        });
    };

    render() {
        const { from } = this.props.location.state || { from: { pathname: "/" } };
        const { redirectToReferrer } = this.state;

        if (redirectToReferrer) {
            return <Redirect to={from} />;
        }

        return (
            <div>
                <p>You must log in to view the page at {from.pathname}</p>
                <button onClick={this.login}>Log in</button>
            </div>
        );
    }
}



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
            body: formData
        });
    };

    render() {
        return (
            <Router>
                <div>
                    <AuthButton />
                    <ul>
                        <li>
                            <Link to="/public">Public Page</Link>
                        </li>
                        <li>
                            <Link to="/protected">Protected Page</Link>
                        </li>
                        <li>
                            <Link to="/chucky">get me chuck</Link>
                        </li>
                    </ul>
                    <Route path="/public" component={Public} />
                    {/*<Route path="/login" component={Login} />*/}
                    <Route path="/chucky" component={Chucky}/>
                    <PrivateRoute path="/protected" component={Protected} />


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
                        </Grid>
                    </div>

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

                </div>


            </Router>



        );
    }
}

export default App;
