import React from "react";
import Col from "react-bootstrap/es/Col";
import BidTable from "./Bids";
import ScheduleTable from "./ScheduleTable";
import PlayerTable from "./Players";
import OfferTable from "./Offers";
import TradeTable from "./Trades";
import {Grid, Panel, Row} from "react-bootstrap";
import Login from "./Login";
import {Route} from "react-router-dom";

class GameDashboard extends React.Component {
    render () {
        return(
            <Grid>
                <Panel>
                    <Panel.Body>
                        <Row className="show-grid">
                            <Col md={4} >
                                <Panel>
                                    <Panel.Heading>Schedule</Panel.Heading>
                                    <Panel.Body>
                                        <ScheduleTable
                                            currentTime={this.props.data.currentTime}
                                            schedule={this.props.data.schedule}
                                            gameStarted={this.props.data.gameStarted}
                                        />
                                    </Panel.Body>
                                </Panel>
                            </Col>
                            <Col md={4} >
                                <Panel>
                                    <Panel.Heading>Players</Panel.Heading>
                                    <Panel.Body>
                                        <PlayerTable players={this.props.data.players}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                            <Col md={4} >
                                <Panel>
                                    <Panel.Heading>Offers</Panel.Heading>
                                    <Panel.Body>
                                        <OfferTable offers={this.props.data.offers}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                        </Row>
                        <Row className="show-grid">
                            <Col md={6} >
                                <Panel>
                                    <Panel.Heading>Bids</Panel.Heading>
                                    <Panel.Body>
                                        <BidTable bids={this.props.data.bids}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                            <Col md={6} >
                                <Panel>
                                    <Panel.Heading>Trades</Panel.Heading>
                                    <Panel.Body>
                                        <TradeTable trades={this.props.data.trades}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                        </Row>
                    </Panel.Body>
                </Panel>
                <Route path="/login" exact={true} component={Login} />

            </Grid>
        );
    }
}

export default GameDashboard;