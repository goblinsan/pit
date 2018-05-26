import React, {Component} from 'react';
import {Button, Grid, Navbar, Panel, Row, Table} from 'react-bootstrap';
import './App.css';
import Col from "react-bootstrap/es/Col";

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

class ScheduleRow extends React.Component {
    render() {
        const name = this.props.name;
        const value = this.props.value;

        return (
            <tr>
                <td>{name}</td>
                <td>{value}</td>
            </tr>
        );
    }
}

class ScheduleTable extends React.Component {
    render() {
        const rows = [];

        if (this.props.gameStarted) {
            rows.push(
                <ScheduleRow
                    name={"Current Time"}
                    value={this.props.currentTime}
                    key={"currentTime"}/>
            );
        }

        Object.keys(this.props.schedule).forEach((name) => {
            rows.push(
                <ScheduleRow
                    name={name}
                    value={this.props.schedule[name]}
                    key={name}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Event</th>
                    <th>Time</th>
                </tr>
                </thead>
                <tbody>
                {rows}
                </tbody>
            </Table>
        );
    }
}

class PlayerRow extends React.Component {
    render() {
        const player = this.props.player;
        const image = "images/" + this.props.image;
        return (
            <tr>
                <td><img src={image} width={50} alt={"avatar for player: " + player.name}/></td>
                <td>{player.name}</td>
                <td>{player.score}</td>
                <td>{String(player.connected)}</td>
            </tr>
        );
    }
}

class PlayerTable extends React.Component {
    render() {
        const rows = [];

        this.props.players.forEach((player, index) => {
            rows.push(
                <PlayerRow
                    image={String(index + 1) + '.png'}
                    player={player}
                    key={player.name}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th></th>
                    <th>Name</th>
                    <th>Score</th>
                    <th>Connected</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}

class OfferRow extends React.Component {
    render() {
        const offer = this.props.offer;
        return (
            <tr>
                <td>{offer.player.name}</td>
                <td>{offer.amount}</td>
            </tr>
        );
    }
}

class OfferTable extends React.Component {
    render() {
        const rows = [];

        this.props.offers.forEach((offer) => {
            rows.push(
                <OfferRow
                    offer={offer}
                    key={offer.player.name}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}


class BidRow extends React.Component {
    render() {
        const bid = this.props.bid;
        return (
            <tr>
                <td>{bid.requester.name}</td>
                <td>{bid.owner.name}</td>
                <td>{bid.amount}</td>
            </tr>
        );
    }
}

class BidTable extends React.Component {
    render() {
        const rows = [];

        this.props.bids.forEach((bid) => {
            rows.push(
                <BidRow
                    bid={bid}
                    key={bid.requester}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Requester</th>
                    <th>Owner</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}

class TradeRow extends React.Component {
    render() {
        const trade = this.props.trade;
        return (
            <tr>
                <td>{trade.requester}</td>
                <td>{trade.owner}</td>
                <td>{trade.amount}</td>
            </tr>
        );
    }
}

class TradeTable extends React.Component {
    render() {
        const rows = [];

        this.props.trades.forEach((trade) => {
            rows.push(
                <TradeRow
                    trade={trade}
                    key={trade.requester}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Requester</th>
                    <th>Owner</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}


class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            currentTime: null,
            gameStarted: false,
            schedule: [],
            players: [],
            offers: [],
            bids: [],
            trades: []
        };
        this.startGame = this.startGame.bind(this);
        this.getGameInfo = this.getGameInfo.bind(this);
        this.updateCurrentTime = this.updateCurrentTime.bind(this);
        this.getSchedule = this.getSchedule.bind(this);
        this.getPlayers = this.getPlayers.bind(this);
        this.getOffers = this.getOffers.bind(this);
        this.getBids = this.getBids.bind(this);
        this.getTrades = this.getTrades.bind(this);
    }

    startGame() {
        fetch("http://localhost:8080/start").then(() => {
            this.setState({gameStarted: true})
        });
        fetch("http://localhost:8080/scheduleStrings")
            .then(res => res.json()
                .then(result => {
                    this.setState({
                        schedule: result
                    })
                })
            );
        this.getGameInfo();
    }

    getGameInfo() {
        this.updateCurrentTime();
        this.getSchedule();
        this.getPlayers();
        this.getOffers();
        this.getBids();
        this.getTrades();
    }

    getSchedule() {
        fetch("http://localhost:8080/scheduleStrings")
            .then(res => res.json()
                .then(result => {
                    this.setState({
                        schedule: result
                    })
                })
            ).then(() => setTimeout(this.getSchedule, 30000));
    }

    getPlayers() {
        fetch("http://localhost:8080/players")
            .then(res => res.json()
                .then(result => {
                        this.setState({
                            players: result
                        })
                    }
                )).then(() => setTimeout(this.getPlayers, 5000));
    }

    getOffers() {
        fetch("http://localhost:8080/offers")
            .then(res => res.json()
                .then(result => {
                        this.setState({
                            offers: result
                        })
                    }
                )).then(() => setTimeout(this.getOffers, 5000));
    }

    getBids() {
        fetch("http://localhost:8080/bids")
            .then(res => res.json()
                .then(result => {
                        this.setState({
                            bids: result
                        })
                    }
                )).then(() => setTimeout(this.getBids, 5000));
    }

    getTrades() {
        fetch("http://localhost:8080/trades")
            .then(res => res.json()
                .then(result => {
                        this.setState({
                            trades: result
                        })
                    }
                )).then(() => setTimeout(this.getTrades, 5000));
    }

    updateCurrentTime() {
        fetch("http://localhost:8080/time").then(res => res.text()).then((result) => {
            this.setState({currentTime: result})
        }).then(() => setTimeout(this.updateCurrentTime, 1000));
    }

    componentDidMount() {
        this.setState({isLoaded: true});
    }

    render() {
        return (
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
        );
    }
}

export default App;
