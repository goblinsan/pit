import React from "react";
import Col from "react-bootstrap/es/Col";
import BidTable from "./Bids";
import ScheduleTable from "./ScheduleTable";
import PlayerTable from "./Players";
import OfferTable from "./Offers";
import TradeTable from "./Trades";
import {Grid, Panel, Row} from "react-bootstrap";
import Login from "./Login";
import {Redirect, Route} from "react-router-dom";
import ControlPanel from "./ControlPanel";

class GameDashboard extends React.Component {
    render () {
        return(
            <Grid fluid={true}>
                <Panel>
                    <Panel.Body>
                        <Row className="show-grid">
                            <Col md={4} >
                                <Panel>
                                    <Panel.Heading>
                                        Schedule
                                        <div className="pull-right">
                                            Server Time : {this.props.gameData.currentTime}
                                        </div>
                                    </Panel.Heading>
                                    <Panel.Body>
                                        <ScheduleTable
                                            schedule={this.props.gameData.schedule}
                                            gameStarted={this.props.gameData.gameStarted}
                                        />
                                    </Panel.Body>
                                </Panel>
                            </Col>
                            <Col md={4} >
                                <Panel>
                                    <Panel.Heading>Players</Panel.Heading>
                                    <Panel.Body>
                                        <PlayerTable players={this.props.gameData.players}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                            <Col md={4} >
                                <Panel>
                                    <Panel.Heading>Offers</Panel.Heading>
                                    <Panel.Body>
                                        <OfferTable offers={this.props.gameData.offers}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                        </Row>
                        <Row className="show-grid">
                            <Col md={6} >
                                <Panel>
                                    <Panel.Heading>Bids</Panel.Heading>
                                    <Panel.Body>
                                        <BidTable bids={this.props.gameData.bids}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                            <Col md={6} >
                                <Panel>
                                    <Panel.Heading>Trades</Panel.Heading>
                                    <Panel.Body>
                                        <TradeTable trades={this.props.gameData.trades}/>
                                    </Panel.Body>
                                </Panel>
                            </Col>
                        </Row>
                    </Panel.Body>
                </Panel>
                <Route path="/dashboard" exact={true}
                       render={(routerProps) =>
                           this.props.gameData.isAuthenticated === true ? ( <ControlPanel updateSchedule={this.props.updateSchedule}/>) :
                               (<Redirect
                                   to={{
                                       pathname: "/login",
                                       state: {from: routerProps.location}
                                   }}
                               />)
                       }
                />

                <Route path="/login" exact={true}
                       render={() => <Login onLogin={this.props.onLogin} history={this.props.history} /> }
                />

            </Grid>
        );
    }
}

export default GameDashboard;